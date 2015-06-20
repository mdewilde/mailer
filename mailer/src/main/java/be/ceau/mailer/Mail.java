/*
	Copyright 2015 Marceau Dewilde <m@ceau.be>

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

		http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/
package be.ceau.mailer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Mail implements Serializable {

	private static final long serialVersionUID = 201505121202L;

	private InternetAddress from;
	private InternetAddress to;
	private final List<InternetAddress> ccs;
	private final List<InternetAddress> bccs;
	private String subject;
	private String txt;
	private String html;

	public Mail() {
		ccs = new ArrayList<InternetAddress>();
		bccs = new ArrayList<InternetAddress>();
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is <code>null</code>
	 */
	public void setFrom(InternetAddress from) {
		if (from == null) {
			throw new IllegalArgumentException("from argument can not be null");
		}
		this.from = from;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is not a legal email address
	 */
	public void setFrom(String from) {
		this.from = convert(from);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is <code>null</code>
	 */
	public void setTo(InternetAddress to) {
		if (to == null) {
			throw new IllegalArgumentException("to argument can not be null");
		}
		this.to = to;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is not a legal email address
	 */
	public void setTo(String to) {
		this.to = convert(to);
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public void setText(String txt) {
		this.txt = txt;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is not a legal email address
	 */
	public void addCc(String cc) {
		this.ccs.add(convert(cc));
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is <code>null</code>
	 */
	public void addCc(InternetAddress cc) {
		if (cc == null) {
			throw new IllegalArgumentException("cc argument can not be null");
		}
		this.ccs.add(cc);
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is not a legal email address
	 */
	public void addBcc(String bcc) {
		this.bccs.add(convert(bcc));
	}

	/**
	 * @throws IllegalArgumentException
	 *             if argument is <code>null</code>
	 */
	public void addBcc(InternetAddress bcc) {
		if (bcc == null) {
			throw new IllegalArgumentException("bcc argument can not be null");
		}
		this.bccs.add(bcc);
	}

	private static InternetAddress convert(String email) {
		try {
			return new InternetAddress(email);
		} catch (AddressException e) {
			throw new IllegalArgumentException(email + " is not a valid email address");
		}
	}

	/**
	 * Checks if <code>this</code> Mail instance has the minimum required amount
	 * of information.
	 */
	boolean isValid() {
		return from != null && to != null && (txt != null || html != null);
	}

	@Override
	public String toString() {
		return "Mail [from=" + from + ", to=" + to + ", ccs=" + ccs + ", bccs=" + bccs + ", subject=" + subject + ", txt=" + txt + ", html=" + html + "]";
	}

	InternetAddress getFrom() {
		return from;
	}

	InternetAddress getTo() {
		return to;
	}

	List<InternetAddress> getCcs() {
		return ccs;
	}

	List<InternetAddress> getBccs() {
		return bccs;
	}

	String getSubject() {
		return subject;
	}

	String getTxt() {
		return txt;
	}

	String getHtml() {
		return html;
	}

}