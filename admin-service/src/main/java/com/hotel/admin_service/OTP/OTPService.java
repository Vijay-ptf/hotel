package com.hotel.admin_service.OTP;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


@Service
public class OTPService {
    private final Map<String, Integer> storeOtp = new HashMap<>();
    private final Map<String, Long> otpCreationTime = new HashMap<>();
    private static final long OTP_VALID_DURATION = 5 * 60 * 1000;

    public int generateAndSaveOtp(String email) {
        int otp = new Random().nextInt(900000) + 100000;
        storeOtp.put(email, otp);
        otpCreationTime.put(email, System.currentTimeMillis());
        return otp;
    }

    public boolean verifyOtp(String email, int otp) {
        Integer storedOTP = storeOtp.get(email);
        Long creationTime = otpCreationTime.get(email);

        if (storedOTP == null || creationTime == null) {
            return false;
        }

        if (System.currentTimeMillis() - creationTime > OTP_VALID_DURATION) {
            storeOtp.remove(email);
            otpCreationTime.remove(email);
            return false;
        }

        return storedOTP.equals(otp);
    }

    public void clearOtp(String email) {
        storeOtp.remove(email);
        otpCreationTime.remove(email);
    }

    public boolean resendOtp(String email) {
        if (storeOtp.containsKey(email)) {
            int newOtp = generateAndSaveOtp(email);
            return true;
        }
        return false;
    }
}