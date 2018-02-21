package org.hkfree.ospf.tools.ssh;

import static net.sf.expectit.matcher.Matchers.contains;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Shell;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.sf.expectit.Expect;
import net.sf.expectit.ExpectBuilder;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
public class SshClient {
	
    private String host = "192.168.0.1";
    private int port = 22;
    private int timeout = 5000;
    private String username = "admin";
    private String password = "admin";
    
    public SshClient(String host, int port, String username, String password, int timeout) {
		this.username = username;
		this.port = port;
		this.password = password;
		this.host = host;
		this.timeout = timeout;
	}
	
	public String RoxQuesryOspfStatus(String maintPassword) throws IOException {
		StringBuilder sb = new StringBuilder();
		final SSHClient ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());

        ssh.connect(host, port);
        try {
            ssh.authPassword(username, password);
            final Session session = ssh.startSession();
            
            session.allocateDefaultPTY();
            Shell shell = session.startShell();
            Expect expect = new ExpectBuilder()
                    .withOutput(shell.getOutputStream())
                    .withInputs(shell.getInputStream(), shell.getErrorStream())
                    .withTimeout(timeout, TimeUnit.MILLISECONDS)
                    .build();
            try {
                
                expect.sendLine("maint-login");
                expect.expect(contains("Password:"));
                expect.sendLine(maintPassword);
                expect.expect(contains("~#"));
                expect.sendLine("vtysh -c \"show ip ospf database network\"");
                String net = expect.expect(contains("~#")).getBefore();
                expect.sendLine("vtysh -c \"show ip ospf database router\"");
                String router = expect.expect(contains("~#")).getBefore();
                expect.sendLine("vtysh -c \"show ip ospf database external\"");
                String external = expect.expect(contains("~#")).getBefore();
                expect.sendLine("exit");
                sb.append(net.replace("\r\r\n", "\r\n"));
                sb.append(router.replace("\r\r\n", "\r\n"));
                sb.append(external.replace("\r\r\n", "\r\n"));
            } finally {
                session.close();
                ssh.close();
                expect.close();
            }
        } finally {
            ssh.disconnect();
        }
        return sb.toString();
    }
}
