package main.java;

import javax.mail.*;
import javax.mail.search.*;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NirmataMailer {
    private Session session;
    private Store store;
    private Folder folder;
    private String host, email, password, folderName;


    public NirmataMailer(String host, String protocol, String folderName, String email, String password) {
        this.host = host;
        this.email = email;
        this.password = password;
        this.folderName = folderName;
        Properties props = System.getProperties();
        props.setProperty("mail.store.protocol", protocol);
        session = Session.getDefaultInstance(props, null);
        try { store = session.getStore(protocol); }
        catch (Exception e) {}
    }

    public String getActivationLink(Date testStartTime, long time_in_ms) {
        String checkFrom = "support@nirmata.com";
        String checkSubj = "Welcome to Nirmata!";
        String query = "https://www.nirmata.io/security/setPassword.html?action=activate&token=";
        String pattern = "(?<=" + query + ")?(\\w{144})";
        String result = "";

        try { result = ParseMail(checkFrom, checkSubj, pattern, testStartTime, time_in_ms); }
        catch (Exception e) { return "";}
        finally { return (query + result); }
    }

    public String getAccessCode(Date testStartTime, long time_in_ms) {
        String checkFrom = "support@nirmata.com";
        String checkSubj = "Your Nirmata Account";
        String pattern = "(?<=Access code:)?([0-9]{6})";
        String result = "";

        try { result = ParseMail(checkFrom, checkSubj, pattern, testStartTime, time_in_ms); }
        catch (Exception e) {}
//        catch (Exception e) { return "";}
        finally { return (result); }
    }


    private String ParseMail(String checkFrom, String checkSubj, String pattern, Date testStartTime, long time_in_ms) throws MessagingException, IOException, InterruptedException {
        String res = "";

        while (new Date().getTime() < (testStartTime.getTime() + time_in_ms)) {     // try to retrieve while time is less than time_in_ms
            store.connect(host, email, password);
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.search (new SubjectTerm(checkSubj),
                    folder.search(new FromStringTerm(checkFrom)));

            if (messages.length > 0) {
                Message message = messages[messages.length-1];      // simplyer version, getting just last message - suppose to be working
                if (message.getReceivedDate().compareTo(testStartTime) > 0) {
                    String m = getLastMessage(messages).getContent().toString();
                    Pattern pa = Pattern.compile(pattern);
                    Matcher ma = pa.matcher(m);
                    while (ma.find()) res = m.substring(ma.start(), ma.end());
                }
            }
            folder.close(false);
            store.close();
            if (res=="") Thread.sleep(time_in_ms/60);
            else break;
        }

        return res;
    }



    public String getActivationLink (String checkFrom, String checkSubj, Date testStartTime, long time_in_ms) throws MessagingException, IOException, InterruptedException {
        String res = "";
        String query = "https://www.nirmata.io/security/setPassword.html?action=activate&token=";
        String pattern = "(?<=" + query + ")?(\\w{144})";

        while (new Date().getTime() < (testStartTime.getTime() + time_in_ms)) {     // try to retrieve while time is less than time_in_ms
            store.connect(host, email, password);
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.search (new SubjectTerm(checkSubj),
                    folder.search(new FromStringTerm(checkFrom)));

            if (messages.length > 0) {
                Message message = messages[messages.length-1];      // simplyer version, getting just last message - suppose to be working
                if (message.getReceivedDate().compareTo(testStartTime) > 0) {
                    String m = getLastMessage(messages).getContent().toString();
                    Pattern pa = Pattern.compile(pattern);
                    Matcher ma = pa.matcher(m);
                    while (ma.find()) res = m.substring(ma.start(), ma.end());
                }
            }
            folder.close(false);
            store.close();
            if (res=="") Thread.sleep(5000);
            else break;
        }

        return (query + res);

    }


    public String getCode (String checkFrom, String checkSubj, Date testStartTime, long time_in_ms) throws MessagingException, IOException, InterruptedException {
        String res = "";
        String pattern = "(?<=Access code:)?([0-9]{6})";

        while (new Date().getTime() < (testStartTime.getTime() + time_in_ms)) {     // try to retrieve while time is less than time_in_ms
            store.connect(host, email, password);
            folder = store.getFolder(folderName);
            folder.open(Folder.READ_ONLY);
            Message[] messages = folder.search (new SubjectTerm(checkSubj),
                    folder.search(new FromStringTerm(checkFrom)));

            if (messages.length > 0) {
                Message message = messages[messages.length-1];      // simplyer version, getting just last message - suppose to be working
                if (message.getReceivedDate().compareTo(testStartTime) > 0) {
                    String m = getLastMessage(messages).getContent().toString();
                    Pattern pa = Pattern.compile(pattern);
                    Matcher ma = pa.matcher(m);
                    while (ma.find()) res = m.substring(ma.start(), ma.end());
                }
            }
            folder.close(false);
            store.close();
            if (res=="") Thread.sleep(5000);
            else break;
        }

        return res;
    }

    // unused because I found that messages are sorted descending (newest at the end)
    private Message getLastMessage (Message[] messages) throws MessagingException {
        int ind = 0;
        Date d = new Date(1);

        for (int i = 0; i < messages.length; i++) {
            if (messages[i].getSentDate().getTime() > d.getTime()) {
                ind = i;
                d = messages[i].getSentDate();
            }
        }
        return messages[ind];
    }
}
