package nablarch.core.message;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.junit.Test;


public class ApplicationExceptionTest {

    @Test
    public void testConstructor() {

        Message msg1 = new Message(MessageLevel.ERROR, new MockMessage("MSG00001", "message1"));
        Message msg2 = new Message(MessageLevel.ERROR, new MockMessage("MSG00002", "message2"));
        Message msg3 = new Message(MessageLevel.ERROR, new MockMessage("MSG00003", "message3"));
        Message msg4 = new Message(MessageLevel.ERROR, new MockMessage("MSG00004", "message4"));
        Message msg5 = new Message(MessageLevel.ERROR, new MockMessage("MSG00005", "message5"));

        ApplicationException ae1 = new ApplicationException(msg1);

        assertEquals("MSG00001", ae1.getMessages()
                                    .get(0)
                                    .getMessageId());

        List<Message> messages1 = new ArrayList<Message>();
        messages1.add(msg1);
        messages1.add(msg2);
        messages1.add(msg3);

        List<Message> messages2 = new ArrayList<Message>();
        messages2.add(msg4);
        messages2.add(msg5);

        ApplicationException ae2 = new ApplicationException(messages1);

        assertEquals("MSG00001", ae2.getMessages()
                                    .get(0)
                                    .getMessageId());
        assertEquals("MSG00002", ae2.getMessages()
                                    .get(1)
                                    .getMessageId());
        assertEquals("MSG00003", ae2.getMessages()
                                    .get(2)
                                    .getMessageId());

        assertEquals("message1\nmessage2\nmessage3\n", ae2.getMessage());


        ApplicationException ae3 = new ApplicationException(messages1);
        ae3.addMessages(messages2);

        assertEquals("MSG00001", ae3.getMessages()
                                    .get(0)
                                    .getMessageId());
        assertEquals("MSG00002", ae3.getMessages()
                                    .get(1)
                                    .getMessageId());
        assertEquals("MSG00003", ae3.getMessages()
                                    .get(2)
                                    .getMessageId());
        assertEquals("MSG00004", ae3.getMessages()
                                    .get(3)
                                    .getMessageId());
        assertEquals("MSG00005", ae3.getMessages()
                                    .get(4)
                                    .getMessageId());

        assertEquals("message1\nmessage2\nmessage3\nmessage4\nmessage5\n", ae3.getMessage());
    }

    private static class MockMessage implements StringResource {

        public MockMessage(String id, String format) {
            super();
            this.id = id;
            this.format = format;
        }

        private String id;

        private String format;

        public String getId() {
            return id;
        }

        public String getValue(Locale lang) {
            return format;
        }

    }
}
