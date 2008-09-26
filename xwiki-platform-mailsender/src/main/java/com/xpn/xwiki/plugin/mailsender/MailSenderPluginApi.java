/*
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.xpn.xwiki.plugin.mailsender;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.velocity.VelocityContext;

import com.xpn.xwiki.XWikiContext;
import com.xpn.xwiki.api.Attachment;
import com.xpn.xwiki.api.XWiki;
import com.xpn.xwiki.plugin.PluginApi;

/**
 * Plugin that brings powerful mailing capabilities. This is the wrapper accessible from in-document scripts.
 * 
 * @see MailSender
 * @version $Id$
 */
public class MailSenderPluginApi extends PluginApi<MailSenderPlugin> implements MailSender
{
    /**
     * Log object to log messages in this class.
     */
    private static final Log LOG = LogFactory.getLog(MailSenderPluginApi.class);

    /**
     * API constructor.
     * 
     * @param plugin The wrapped plugin object.
     * @param context Context of the request.
     * @see PluginApi#PluginApi(com.xpn.xwiki.plugin.XWikiPluginInterface,XWikiContext)
     */
    public MailSenderPluginApi(MailSenderPlugin plugin, XWikiContext context)
    {
        super(plugin, context);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#sendHtmlMessage(String, String, String, String, String, String, String, java.util.List)
     */
    public int sendHtmlMessage(String from, String to, String cc, String bcc, String subject, String body,
        String alternative, List<Attachment> attachments)
    {
        Mail email = new Mail();
        email.setSubject(subject);
        email.setFrom(from);
        email.setTo(to);
        email.setCc(cc);
        email.setBcc(bcc);
        email.setTextPart(alternative);
        email.setHtmlPart(body);
        email.setAttachments(attachments);
        return sendMail(email);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#sendTextMessage(String, String, String, String)
     */
    public int sendTextMessage(String from, String to, String subject, String message)
    {
        Mail email = new Mail();
        email.setSubject(subject);
        email.setTextPart(message);
        email.setFrom(from);
        email.setTo(to);
        return sendMail(email);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#sendTextMessage(String, String, String, String, String, String, java.util.List)
     */
    public int sendTextMessage(String from, String to, String cc, String bcc, String subject, String message,
        List<Attachment> attachments)
    {
        Mail email = new Mail();
        email.setSubject(subject);
        email.setTextPart(message);
        email.setFrom(from);
        email.setTo(to);
        email.setCc(cc);
        email.setBcc(bcc);
        email.setAttachments(attachments);
        return sendMail(email);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#sendMessageFromTemplate(String, String, String, String, String, String, VelocityContext)
     */
    public int sendMessageFromTemplate(String from, String to, String cc, String bcc, String language,
        String documentFullName, VelocityContext vcontext)
    {
        try {
            return getProtectedPlugin().sendMailFromTemplate(documentFullName, from, to, cc, bcc, language, vcontext,
                context);
        } catch (Exception e) {
            context.put("error", e.getMessage());
            LOG.error("sendMessageFromTemplate", e);
            return -1;
        }
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#createMail()
     */
    public Mail createMail()
    {
        return new Mail();
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#sendMail(Mail)
     */
    public int sendMail(Mail mail)
    {
        int result = 0;
        try {
            getProtectedPlugin().sendMail(mail, context);
        } catch (Exception e) {
            context.put("error", e.getMessage());
            LOG.error("Failed to send email [" + mail.toString() + "]", e);
            result = -1;
        }

        return result;
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#createMailConfiguration(com.xpn.xwiki.api.XWiki)
     */
    public MailConfiguration createMailConfiguration(XWiki xwiki)
    {
        return new MailConfiguration(xwiki);
    }

    /**
     * {@inheritDoc}
     * 
     * @see MailSender#sendMail(Mail, MailConfiguration)
     */
    public int sendMail(Mail mail, MailConfiguration mailConfiguration)
    {
        int result = 0;
        try {
            getProtectedPlugin().sendMail(mail, mailConfiguration, context);
        } catch (Exception e) {
            context.put("error", e.getMessage());
            LOG.error("Failed to send email [" + mail.toString() + "] using mail configuration ["
                + mailConfiguration.toString() + "]", e);
            result = -1;
        }

        return result;
    }
}
