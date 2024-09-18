package game.tele;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.result.UpdateResult;
import game.dto.data.UserTele;
import game.repository.MongoDBConnectionFactory;
import org.bson.Document;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

@Service
public class TeleAuthentication extends TelegramLongPollingBot {

    int RATE_LIMIT = 30;

    LinkedBlockingQueue<Integer> blockingQueue = new LinkedBlockingQueue<>(30);
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private SecureRandom random = new SecureRandom();

    @PostConstruct
    public void init() {
        scheduler.scheduleAtFixedRate(() -> {
            blockingQueue.clear();
            for (int i = 0; i < RATE_LIMIT; i++) {
                blockingQueue.offer(i);
            }
        }, 0, 1, TimeUnit.SECONDS);
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            UserTele u = getInfoByChatID(chatId);

            String text = message.getText();
            String textMessage = "";
            if (text.contains("/start")) {
                String[] parts = text.split("\\s+");
                if (parts.length > 1) {
                    String nickname = parts[1];
                    String phone = getPhoneByNickname(nickname);
                    if (u != null) {
                        if (Objects.equals(u.getNickname(), nickname) && u.isActive()) {
                            textMessage = "Tele đã liên kết với 1 tài khoản khác, hãy thử bằng 1 tele khác";
                            sendPhoneAndOTPRequest(chatId, textMessage);
                            return;
                        }
                    }
                    if (phone.isEmpty()) {
                        textMessage = "Vui lòng xác thực số điện thoại để sử dụng dịch vụ";
                        sendPhoneAndOTPRequest(chatId, textMessage);
                    } else {
                        if (u != null) {
                            textMessage = "Xin chào " + u.getNickname();
                        } else {
                            textMessage = "Chào mừng " + message.getFrom().getFirstName() + " đến với hệ thống OTP miễn phí." + "\n" + "Để nhận OTP miễn phí vui lòng ấn nút 'Chia sẻ số điện thoại' bên dưới để xác thực tài khoản";
                        }
                        sendPhoneAndOTPRequest(chatId, textMessage);
                        UserTele userTele = getInfoByChatID(chatId);
                        if (userTele == null) {
                            saveUserInfo(nickname, chatId);
                        } else if (userTele.getPhoneNumber().isEmpty() || !userTele.isActive()) {
                            textMessage = "Chào mừng " + userTele.getNickname() + " đến với hệ thống OTP miễn phí." + "\n" + "Để nhận OTP miễn phí vui lòng ấn nút 'Chia sẻ số điện thoại' bên dưới để xác thực tài khoản";
                            sendPhoneAndOTPRequest(chatId, textMessage);
                        } else {
                            String otp = generateOTP();
                            saveOTP(chatId, otp);
                            saveOTPPhone(chatId, otp);
                            sendOTP(chatId, otp);
                        }
                    }
                } else {
                    processUser(u, chatId);
                }
            }
            if (message.getText().equals("Lấy lại mã kích hoạt")) {
                if (u == null || !u.isActive() || u.getPhoneNumber() == null || u.getPhoneNumber().isEmpty()) {
                    textMessage = "Vui lòng xác thực số điện thoại để sử dụng dịch vụ";
                    sendPhoneAndOTPRequest(chatId, textMessage);
                } else {
                    String otp = generateOTP();
                    saveOTP(chatId, otp);
                    saveOTPPhone(chatId, otp);
                    sendOTP(chatId, otp);
                }
            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            if ("get_otp" .equals(callbackData)) {
                String otp = generateOTP();
                saveOTP(chatId, otp);
                saveOTPPhone(chatId, otp);
                sendOTP(chatId, otp);
            }
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            Contact contact = update.getMessage().getContact();
            String phoneNumber = contact.getPhoneNumber();
            String chatId = String.valueOf(update.getMessage().getChatId());
            handlePhoneNumber(chatId, phoneNumber);
        }
    }

    private void sendMessage(String chatId, String textMessage) {
        SendMessage msg = new SendMessage();
        msg.setChatId(chatId);
        msg.setText(textMessage);
        try {
            execute(msg);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void processUser(UserTele u, String chatId) {
        if (u == null) {
            sendMessage(chatId, "Vui lòng xác thực số điện thoại để sử dụng dịch vụ");
        } else if (!u.isActive()) {
            sendMessage(chatId, "Vui lòng xác thực tele để sử dụng dịch vụ");
        } else if (u.getPhoneNumber().isEmpty()) {
            sendMessage(chatId, "Vui lòng xác thực số điện thoại để sử dụng dịch vụ");
        } else {
            String otp = generateOTP();
            saveOTP(chatId, otp);
            saveOTPPhone(chatId, otp);
            sendOTP(chatId, otp);
        }
    }

    private void sendPhoneAndOTPRequest(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);
        List<KeyboardRow> keyboard = new ArrayList<>();
        KeyboardRow phoneRow = new KeyboardRow();
        KeyboardButton phoneButton = new KeyboardButton();
        phoneButton.setText("Chia sẻ số điện thoại");
        phoneButton.setRequestContact(true); // Yêu cầu người dùng chia sẻ số điện thoại
        phoneRow.add(phoneButton);
        keyboard.add(phoneRow);
        KeyboardRow otpRow = new KeyboardRow();
        KeyboardButton otpButton = new KeyboardButton();
        otpButton.setText("Lấy lại mã kích hoạt");
        otpRow.add(otpButton);
        keyboard.add(otpRow);
        keyboardMarkup.setKeyboard(keyboard);
        message.setReplyMarkup(keyboardMarkup);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getPhoneByNickname(String nickname) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname);
        FindIterable<Document> result = collection.find(filter);
        Iterator<Document> iterator = result.iterator();
        if (iterator.hasNext()) {
            Document document = iterator.next();
            return document.getString("phone");
        } else {
            return "";
        }
    }

//    private UserTele getInfoByNickname(String nickname) {
//        MongoDatabase db = MongoDBConnectionFactory.getDB();
//        MongoCollection<Document> collection = db.getCollection("user_tele");
//        Document filter = new Document("nickname", nickname);
//        MongoCursor<Document> cursor = collection.find(filter).iterator();
//
//        try {
//            if (cursor.hasNext()) {
//                Document doc = cursor.next();
//                UserTele user = extractUserInfo(doc);
//                return user;
//            } else {
//                return null;
//            }
//        } finally {
//            cursor.close();
//        }
//
//    }

    private UserTele getInfoByChatID(String chatID) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("chatID", chatID);
        MongoCursor<Document> cursor = collection.find(filter).iterator();
        try {
            if (cursor.hasNext()) {
                Document doc = cursor.next();
                return extractUserInfo(doc);
            } else {
                return null;
            }
        } finally {
            cursor.close();
        }
    }

    private UserTele extractUserInfo(Document doc) {
        String id = doc.getObjectId("_id").toString();
        String nickname = doc.getString("nickname");
        String phoneNumber = doc.getString("phoneNumber");
        boolean isActive = doc.getBoolean("isActive");
        String otp = doc.getString("otp");
        long timeToExpired = doc.getInteger("timeToExpired");
        String createdDate = doc.getString("createdDate");
        String chatID = doc.getString("chatID");
        UserTele user = new UserTele();
        user.setId(id);
        user.setNickname(nickname);
        user.setPhoneNumber(phoneNumber);
        user.setActive(isActive);
        user.setOtp(otp);
        user.setTimeToExpired(timeToExpired);
        user.setCreatedDate(createdDate);
        user.setChatID(chatID);
        return user;
    }


    private void saveUserInfo(String nickname, String chatId) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document document = new Document();
        document.put("nickname", nickname);
        document.put("chatID", chatId);
        document.put("phoneNumber", "");
        document.put("isActive", false);
        document.put("otp", "");
        document.put("timeToExpired", 0);

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formattedDate = dateFormat.format(new Date());
        document.put("createdDate", formattedDate);
        collection.insertOne(document);
    }


    private void saveOTP(String chatId, String otp) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("chatID", chatId);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Document updateDocument = new Document("$set", new Document("otp", otp).append("timeToExpired", 300000).append("createdDate", dateFormat.format(date)));
        collection.updateOne(filter, updateDocument);
    }


    private void savePhone(String chatId, String phone) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("chatID", chatId);
        Document updateDocument = new Document("$set", new Document("phoneNumber", phone));
        collection.updateOne(filter, updateDocument);
    }

    private void saveOTPPhone(String chatId, String otp) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document filter = new Document("chatID", chatId);
        Document updateDocument = new Document("$set", new Document("otp", otp));
        collection.updateOne(filter, updateDocument);
    }

    private void handlePhoneNumber(String chatId, String phoneNumber) {
        try {
            SendMessage message = new SendMessage();
            message.setChatId(chatId);
            UserTele userTele = getInfoByChatID(chatId);
            String phone;
            if (userTele != null) {
                phone = getPhoneByNickname(userTele.getNickname());
            } else {
                message.setText("Vui lòng xác thực tele để sử dụng dịch vụ.");
                execute(message);
                return;
            }
            if (normalizePhoneNumber(phone).equals(normalizePhoneNumber(phoneNumber))) {
                savePhone(chatId, phoneNumber);
                String otp = generateOTP();
                saveOTP(chatId, otp);
                saveOTPPhone(userTele.getNickname(), otp, phoneNumber);
                sendOTPActivePhone(chatId, otp);
            } else {
                message.setText("Số điện thoại không khớp, vui lòng thử lại.");
                execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void saveOTPPhone(String nickname, String otp, String phone) {
        MongoDatabase db = MongoDBConnectionFactory.getDB();
        MongoCollection<Document> collection = db.getCollection("user_phone");
        Document filter = new Document("nickname", nickname);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        Document updateDocument = new Document("$set", new Document("otp", otp).append("timeToExpired", 300000).append("createdDate", dateFormat.format(date)).append("phone", phone));

        UpdateResult result = collection.updateOne(filter, updateDocument);
        if (result.getMatchedCount() == 0) {
            Document newDocument = new Document("nickname", nickname).append("isActive", false).append("otp", otp).append("phone", phone).append("timeToExpired", 300000).append("createdDate", dateFormat.format(date));
            collection.insertOne(newDocument);
        }
    }

    private static String normalizePhoneNumber(String phoneNumber) {
        if (phoneNumber == null) {
            return "";
        }
        phoneNumber = phoneNumber.trim().replaceAll("\\s+", " ");
        // Loại bỏ dấu "+" nếu có
        if (phoneNumber.startsWith("+")) {
            phoneNumber = phoneNumber.substring(1);
        }
        // Nếu bắt đầu bằng '84' (mã quốc gia Việt Nam), chuyển thành '0'
        if (phoneNumber.startsWith("84")) {
            phoneNumber = "0" + phoneNumber.substring(2);
        }
        return phoneNumber;
    }


    private String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private void sendOTPActivePhone(String chatId, String otp) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Cảm ơn bạn đã chia sẻ số điện thoại" + "\n" + "Mã OTP của bạn là : " + otp + " và có hiệu lực trong vòng 5 phút." + "\n" + "Vui lòng hoàn tất đăng ký và kết nối lại để chơi game." + "\n" + "Xin cảm ơn.");
        try {
            if (blockingQueue.poll(10, TimeUnit.SECONDS) != null) {
                execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private void sendOTP(String chatId, String otp) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Mã OTP của bạn là : " + otp + " và có hiệu lực trong vòng 5 phút.");
        try {
            if (blockingQueue.poll(10, TimeUnit.SECONDS) != null) {
                execute(message);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getBotUsername() {
        return "BonWin";
    }

    @Override
    public String getBotToken() {
        return "6831621160:AAHPfkEON1-u2e44F8WAVdu5vT9ySql8ztA";
    }
}
