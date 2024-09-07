package game.tele;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
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

import java.security.SecureRandom;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Service
public class TeleAuthentication extends TelegramLongPollingBot {

    private SecureRandom random = new SecureRandom();

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            String chatId = message.getChatId().toString();
            String text = message.getText();
            String textMessage = "";
            if (text.contains("/start")) {
                UserTele u = getInfoByChatID(chatId);
                String[] parts = text.split("\\s+");
                if (parts.length > 1) {
                    String nickname = parts[1];
                    if (u != null && !Objects.equals(u.getNickname(), nickname) && u.isActive()) {
                        textMessage = "Tele đã liên kết với 1 tài khoản khác, hãy thử bằng 1 tele khác";
                        sendPhoneAndOTPRequest(chatId, textMessage);
                    }
                    textMessage = "Xin chào " + message.getFrom().getFirstName();
                    sendPhoneAndOTPRequest(chatId, textMessage);
                    UserTele userTele = getInfoByChatID(chatId);
                    if (userTele == null) {
                        saveUserInfo(nickname, chatId);
                    }
                    String otp = generateOTP();
                    sendOTP(chatId, otp);
                    saveOTP(chatId, otp);
                } else {
                    String otp = generateOTP();
                    sendOTP(chatId, otp);
                    saveOTP(chatId, otp);
                }
            }
            if (message.getText().equals("Lấy lại mã kích hoạt")) {
                UserTele u = getInfoByChatID(chatId);
                if (u == null || !u.isActive()) {
                    textMessage = "Vui lòng xác thực tele để sử dụng dịch vụ";
                    sendPhoneAndOTPRequest(chatId, textMessage);
                } else {
                    String otp = generateOTP();
                    sendOTP(chatId, otp);
                    saveOTP(chatId, otp);
                }

            }
        } else if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String callbackData = callbackQuery.getData();
            String chatId = callbackQuery.getMessage().getChatId().toString();
            if ("get_otp".equals(callbackData)) {
                String otp = generateOTP();
                sendOTP(chatId, otp);
                saveOTP(chatId, otp);
            }
        } else if (update.hasMessage() && update.getMessage().hasContact()) {
            Contact contact = update.getMessage().getContact();
            String phoneNumber = contact.getPhoneNumber();
            String chatId = String.valueOf(update.getMessage().getChatId());
            handlePhoneNumber(chatId, phoneNumber);
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
                UserTele user = extractUserInfo(doc);
                return user;
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
        MongoDatabase db = MongoDBConnectionFactory.`getDB();
        MongoCollection<Document> collection = db.getCollection("user_tele");
        Document document = new Document();
        document.put("nickname", nickname);
        document.put("chatID", chatId);
        document.put("phoneNumber", "");
        document.put("isActive", false);
        document.put("otp", "");
        document.put("timeToExpired", 0);
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        document.put("createdDate", dateFormat.format(date));
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


    private void handlePhoneNumber(String chatId, String phoneNumber) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        savePhone(chatId, phoneNumber);
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private String generateOTP() {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(random.nextInt(10));
        }
        return otp.toString();
    }

    private void sendOTP(String chatId, String otp) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText("Mã OTP của bạn là : " + otp + " và có hiệu lực trong vòng 5 phút.");
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
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
