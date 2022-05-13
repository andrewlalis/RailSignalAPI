package nl.andrewl.railsignalapi.live.tcp_socket;

import lombok.extern.slf4j.Slf4j;
import nl.andrewl.railsignalapi.live.ComponentDownlink;
import nl.andrewl.railsignalapi.live.ComponentDownlinkService;
import nl.andrewl.railsignalapi.live.ComponentUplinkMessageHandler;
import nl.andrewl.railsignalapi.live.dto.ComponentUplinkMessage;
import nl.andrewl.railsignalapi.util.JsonUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * This link manager is started when a TCP link is established.
 */
@Slf4j
public class TcpLinkManager extends ComponentDownlink implements Runnable {
	private final Socket socket;
	private final ComponentDownlinkService downlinkService;
	private final ComponentUplinkMessageHandler uplinkMessageHandler;

	private final DataOutputStream out;
	private final DataInputStream in;

	public TcpLinkManager(
			long tokenId,
			Socket socket,
			ComponentDownlinkService downlinkService,
			ComponentUplinkMessageHandler uplinkMessageHandler
	) throws IOException {
		super(tokenId);
		this.socket = socket;
		this.downlinkService = downlinkService;
		this.uplinkMessageHandler = uplinkMessageHandler;

		this.out = new DataOutputStream(socket.getOutputStream());
		this.in = new DataInputStream(socket.getInputStream());
	}

	@Override
	public void run() {
		downlinkService.registerDownlink(this);
		while (!socket.isClosed()) {
			try {
				var msg = JsonUtils.readMessage(in, ComponentUplinkMessage.class);
				uplinkMessageHandler.messageReceived(msg);
			} catch (IOException e) {
				log.warn("An error occurred while receiving an uplink message.", e);
			}
		}
		downlinkService.deregisterDownlink(this);
	}

	public void shutdown() {
		try {
			this.socket.close();
		} catch (IOException e) {
			log.warn("An error occurred while closing TCP socket.", e);
		}
	}

	@Override
	public void send(Object msg) throws Exception {
		synchronized (out) {
			JsonUtils.writeJsonString(out, msg);
		}
	}
}
