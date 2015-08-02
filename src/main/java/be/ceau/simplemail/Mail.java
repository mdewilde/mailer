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
package be.ceau.simplemail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Mail implements Serializable {

	private static final long serialVersionUID = 201506201202L;

	private InternetAddress from;
	private InternetAddress to;
	private final List<InternetAddress> ccs;
	private final List<InternetAddress> bccs;
	private String subject;
	private String txt;
	private String html;

	/**
	 * Constructs a new empty Mail instance
	 */
	public Mail() {
		ccs = new ArrayList<InternetAddress>();
		bccs = new ArrayList<InternetAddress>();
	}

	/**
	 * @param from a valid, non-null {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is <code>null</code>
	 */
	public Mail from(InternetAddress from) {
		if (from == null) {
			throw new IllegalArgumentException("from argument can not be null");
		}
		this.from = from;
		return this;
	}

	/**
	 * @param from a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is not a legal email address
	 */
	public Mail from(String from) {
		this.from = convert(from);
		return this;
	}

	/**
	 * @param to a valid, non-null {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is <code>null</code>
	 */
	public Mail to(InternetAddress to) {
		if (to == null) {
			throw new IllegalArgumentException("to argument can not be null");
		}
		this.to = to;
		return this;
	}

	/**
	 * @param to a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is not a legal email address
	 */
	public Mail to(String to) {
		this.to = convert(to);
		return this;
	}

	/**
	 * @param subject a String (no restrictions)
	 * @return this Mail instance, to allow for method chaining
	 */
	public Mail withSubject(String subject) {
		this.subject = subject;
		return this;
	}

	/**
	 * @param txt a String (no restrictions)
	 * @return this Mail instance, to allow for method chaining
	 */
	public Mail withText(String txt) {
		this.txt = txt;
		return this;
	}

	/**
	 * @param html a String (no restrictions)
	 * @return this Mail instance, to allow for method chaining
	 */
	public Mail withHtml(String html) {
		this.html = html;
		return this;
	}

	/**
	 * @param cc a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is not a legal email address
	 */
	public Mail addCc(String cc) {
		this.ccs.add(convert(cc));
		return this;
	}

	/**
	 * @param cc a valid, non-null {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is <code>null</code>
	 */
	public Mail addCc(InternetAddress cc) {
		if (cc == null) {
			throw new IllegalArgumentException("cc argument can not be null");
		}
		this.ccs.add(cc);
		return this;
	}

	/**
	 * @param bcc a valid, non-blank String that can be parsed as {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is not a legal email address
	 */
	public Mail addBcc(String bcc) {
		this.bccs.add(convert(bcc));
		return this;
	}

	/**
	 * @param bcc a valid, non-null {@link javax.mail.internet.InternetAddress}
	 * @return this Mail instance, to allow for method chaining
	 * @throws IllegalArgumentException if argument is <code>null</code>
	 */
	public Mail addBcc(InternetAddress bcc) {
		if (bcc == null) {
			throw new IllegalArgumentException("bcc argument can not be null");
		}
		this.bccs.add(bcc);
		return this;
	}

	private InternetAddress convert(String email) {
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