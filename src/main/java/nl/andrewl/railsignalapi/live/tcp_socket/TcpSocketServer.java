package nl.andrewl.railsignalapi.live.tcp_socket;

import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.live.ComponentUplinkMessageHandler;
import nl.andrewl.railsignalapi.model.LinkToken;
import nl.andrewl.railsignalapi.service.LinkTokenService;
import nl.andrewl.railsignalapi.util.JsonUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * A plain TCP server socket which can be used to connect to components that
 * don't have access to a full websocket client implementation. Instead of the
 * standard interceptor -> handler workflow for incoming connections, this
 * server simply lets the component link send its token as an initial packet
 * in the socket.
 * <p>
 *     All messages sent in this TCP socket are formatted as length-prefixed
 *     JSON messages, where a 2-byte length is sent, followed by exactly that
 *     many bytes, which can be parsed as a JSON object.
 * </p>
 * <p>
 *     In response to the connection packet, the server will send a
 *     {@link ConnectMessage} response.
 * </p>
 */
@Component
@Slf4j
public class TcpSocketServer {
	private final ServerSocket serverSocket;
	private final Set<TcpLinkManager> linkManagers;

	private final LinkTokenService tokenService;
	private final ComponentDownlinkService componentDownlinkService;
	private final ComponentUplinkMessageHandler uplinkMessageHandler;

	public TcpSocketServer(
			LinkTokenService linkTokenService,
			ComponentDownlinkService componentDownlinkService,
			ComponentUplinkMessageHandler uplinkMessageHandler
	) throws IOException {
		this.tokenService = linkTokenService;
		this.componentDownlinkService = componentDownlinkService;
		this.uplinkMessageHandler = uplinkMessageHandler;

		this.linkManagers = new HashSet<>();
		this.serverSocket = new ServerSocket();
		serverSocket.setReuseAddress(true);
		serverSocket.bind(new InetSocketAddress("localhost", 8081));
	}

	@EventListener(ApplicationReadyEvent.class)
	public void runServer() {
		new Thread(() -> {
			log.info("Starting TCP Socket for Component links at " + serverSocket.getInetAddress());
			while (!serverSocket.isClosed()) {
				try {
					Socket socket = serverSocket.accept();
					initializeConnection(socket);
				} catch (IOException e) {
					if (!e.getMessage().contains("Socket closed")) {
						log.warn("An IOException occurred while waiting to accept a TCP socket connection.", e);
					}
				}
			}
			log.info("TCP Socket has been shut down.");
		}, "TcpSocketThread").start();
	}

	@EventListener(ContextClosedEvent.class)
	public void closeServer() throws IOException {
		serverSocket.close();
		for (var linkManager : linkManagers) {
			linkManager.shutdown();
		}
	}

	private void initializeConnection(Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		DataInputStream in = new DataInputStream(socket.getInputStream());
		short tokenLength = in.readShort();
		String rawToken = new String(in.readNBytes(tokenLength));
		if (rawToken.length() < LinkToken.PREFIX_SIZE) {
			JsonUtils.writeJsonString(out, new ConnectMessage(false, "Invalid or missing token."));
			socket.close();
		} else {
			Optional<LinkToken> optionalToken = tokenService.validateToken(rawToken);
			if (optionalToken.isPresent()) {
				LinkToken token = optionalToken.get();
				JsonUtils.writeJsonString(out, new ConnectMessage(true, "Connection established."));
				var linkManager = new TcpLinkManager(token.getId(), socket, componentDownlinkService, uplinkMessageHandler);
				new Thread(linkManager, "LinkManager-" + token.getId()).start();
				linkManagers.add(linkManager);
			} else {
				JsonUtils.writeJsonString(out, new ConnectMessage(false, "Invalid token."));
				socket.close();
			}
		}
	}
}
