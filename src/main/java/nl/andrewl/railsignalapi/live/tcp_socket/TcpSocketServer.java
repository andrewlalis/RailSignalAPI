package nl.andrewl.railsignalapi.live.tcp_socket;

import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.dao.ComponentAccessTokenRepository;
import nl.andrewl.railsignalapi.model.ComponentAccessToken;
import nl.andrewl.railsignalapi.util.JsonUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * A plain TCP server socket which can be used to connect to components that
 * don't have access to a full websocket client implementation.
 */
@Component
@Slf4j
public class TcpSocketServer {
	private final ServerSocket serverSocket;
	private final ComponentAccessTokenRepository tokenRepository;
	private final PasswordEncoder passwordEncoder;

	public TcpSocketServer(ComponentAccessTokenRepository tokenRepository, PasswordEncoder passwordEncoder) throws IOException {
		this.tokenRepository = tokenRepository;
		this.passwordEncoder = passwordEncoder;
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
					log.warn("An IOException occurred while waiting to accept a TCP socket connection.", e);
				}
			}
		});
	}

	@EventListener(ContextClosedEvent.class)
	public void closeServer() throws IOException {
		serverSocket.close();
	}

	private void initializeConnection(Socket socket) throws IOException {
		DataOutputStream out = new DataOutputStream(socket.getOutputStream());
		DataInputStream in = new DataInputStream(socket.getInputStream());
		int tokenLength = in.readInt();
		String rawToken = new String(in.readNBytes(tokenLength));
		if (rawToken.length() < ComponentAccessToken.PREFIX_SIZE) {
			byte[] respBytes = JsonUtils.toJson(Map.of("message", "Invalid token")).getBytes(StandardCharsets.UTF_8);
			out.writeInt(respBytes.length);
			out.write(respBytes);
			socket.close();
		}
		Iterable<ComponentAccessToken> tokens = tokenRepository.findAllByTokenPrefix(rawToken.substring(0, ComponentAccessToken.PREFIX_SIZE));
		for (var token : tokens) {
			if (passwordEncoder.matches(rawToken, token.getTokenHash())) {

			}
		}
	}
}
