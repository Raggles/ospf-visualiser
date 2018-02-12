package org.hkfree.ospf.tools.ssh;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.TimeUnit;
public class SshClient {
	
    private OutputStream _os = null;
    private InputStream _is = null;
    private Socket _socket;
    private String host = null;
    private int port;
    private int timeout;
    private String username = null;
    private String password = null;
    private StringBuilder _sb = null;
	
	public SshClient(String host, int port, String username, String password, int timeout) {
		this.username = username;
		this.port = port;
		this.password = password;
		this.host = host;
		this.timeout = timeout;
	}
	
	public StringBuilder QuesryOspfStatus() throws IOException {
        //TODO: Implement me.
		final SSHClient ssh = new SSHClient();
        ssh.loadKnownHosts();

        ssh.connect("localhost");
        try {
            ssh.authPublickey(System.getProperty("user.name"));
            final Session session = ssh.startSession();
            try {
                final Command cmd = session.exec("show routing status ospf");
                System.out.println(IOUtils.readFully(cmd.getInputStream()).toString());
                cmd.join(5, TimeUnit.SECONDS);
                System.out.println("\n** exit status: " + cmd.getExitStatus());
            } finally {
                session.close();
            }
        } finally {
            ssh.disconnect();
        }
        return null;
    }
}
