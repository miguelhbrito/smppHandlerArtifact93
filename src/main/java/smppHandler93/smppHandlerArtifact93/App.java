package smppHandler93.smppHandlerArtifact93;

import com.cloudhopper.commons.charset.CharsetUtil;
import com.cloudhopper.smpp.SmppBindType;
import com.cloudhopper.smpp.SmppSession;
import com.cloudhopper.smpp.SmppSessionConfiguration;
import com.cloudhopper.smpp.impl.DefaultSmppClient;
import com.cloudhopper.smpp.pdu.SubmitSm;
import com.cloudhopper.smpp.type.Address;
import com.cloudhopper.smpp.type.LoggingOptions;
import com.cloudhopper.smpp.type.RecoverablePduException;
import com.cloudhopper.smpp.type.SmppBindException;
import com.cloudhopper.smpp.type.SmppChannelException;
import com.cloudhopper.smpp.type.SmppInvalidArgumentException;
import com.cloudhopper.smpp.type.SmppTimeoutException;
import com.cloudhopper.smpp.type.UnrecoverablePduException;

import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	public static Logger log = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		DefaultSmppClient cliente = new DefaultSmppClient();

		SmppSessionConfiguration sessionCfg = new SmppSessionConfiguration();

		sessionCfg.setType(SmppBindType.TRANSCEIVER);
		sessionCfg.setHost("127.0.0.1");
		sessionCfg.setPort(2775);
		sessionCfg.setSystemId("smppclient1");
		sessionCfg.setPassword("password");

		LoggingOptions loggingOpt = new LoggingOptions();

		loggingOpt.setLogPdu(false);
		loggingOpt.setLogBytes(false);
		System.out.println("Digite a mensagem que deseja enviar: ");
		Scanner sc = new Scanner(System.in);
		String line = sc.nextLine();
		sessionCfg.setLoggingOptions(loggingOpt);
		try {
			SmppSession session = cliente.bind(sessionCfg, new MySmppSessionHandler());
			SubmitSm sm = createSubmitSm("Origem", "Destino", line, "UTF-8");
			log.debug("Tentando enviar mensagem !");
			session.submit(sm, TimeUnit.SECONDS.toMillis(60));
			log.debug("Msg enviada");

			TimeUnit.SECONDS.sleep(10);
			log.debug("Destruindo sessao !");
			session.close();
			session.destroy();

			log.debug("Destruindo cliente !");
			cliente.destroy();

		} catch (SmppTimeoutException ex) {
			log.error("{}", ex);
		} catch (SmppChannelException ex) {
			log.error("{}", ex);
		} catch (SmppBindException ex) {
			log.error("{}", ex);
		} catch (UnrecoverablePduException ex) {
			log.error("{}", ex);
		} catch (InterruptedException ex) {
			log.error("{}", ex);
		} catch (RecoverablePduException ex) {
			log.error("{}", ex);
		}
	}

	public static SubmitSm createSubmitSm(String origem, String destino, String msg, String charcode)
			throws SmppInvalidArgumentException {
		SubmitSm sm = new SubmitSm();
		sm.setSourceAddress(new Address((byte) 5, (byte) 0, origem));
		sm.setDestAddress(new Address((byte) 1, (byte) 1, destino));
		sm.setDataCoding((byte) 8);
		sm.setShortMessage(CharsetUtil.encode(msg, charcode));
		sm.setRegisteredDelivery((byte) 1);

		return sm;
	}
}
