package nl.andrewl.railsignalapi.live.tcp_socket;

import nl.andrewl.railsignalapi.live.ComponentDownlink;
import nl.andrewl.railsignalapi.util.JsonUtils;

import java.io.DataOutputStream;
import java.nio.charset.StandardCharsets;

public class TcpDownlink extends ComponentDownlink {
	private final DataOutputStream out;

	public TcpDownlink(long id, DataOutputStream out) {
		super(id);
		this.out = out;
	}

	@Override
	public void send(Object msg) throws Exception {
		byte[] jsonBytes = JsonUtils.toJson(msg).getBytes(StandardCharsets.UTF_8);
		out.writeInt(jsonBytes.length);
		out.write(jsonBytes);
		out.flush();
	}
}
