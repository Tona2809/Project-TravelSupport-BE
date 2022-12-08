package com.hcmute.hotel.service;

import com.hcmute.hotel.config.PaypalConfig;
import com.hcmute.hotel.model.entity.BookingEntity;
import com.paypal.api.payments.*;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaypalService {

    @Autowired()
    private APIContext apiContext;
    @Autowired()
    private PaypalConfig paypalConfig;
    public Payment createPayment(
            BookingEntity booking,
            String currency,
            String method,
            String intent,
            String cancelUrl,
            String successUrl) throws PayPalRESTException {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        amount.setTotal(String.format("%d", booking.getTotalPrice()));
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);

        List<Transaction> transactions = new ArrayList<>();
        transactions.add(transaction);

        Payer payer = new Payer();
        payer.setPaymentMethod(method.toString());
        //Xử lý payerInfo
        PayerInfo payerInfo = payer.getPayerInfo();

//        if (order.getUserOrder().getEmail()!="")
//        {
//            payerInfo.setEmail(order.getUserOrder().getEmail());
//        }
//        payerInfo.setFirstName(order.getName());
//        payerInfo.setLastName("");
//        payerInfo.setMiddleName("");
//        //Xử lý address
//        ShippingAddress address = new ShippingAddress();
//        address.setLine1(order.getAddressOrder().getAddressDetail());
//        address.setDefaultAddress(true);
//        UUID uuid = UUID.randomUUID();
//        String uid = uuid.toString();
//        address.setId(uid);
//        address.setLine2(order.getAddressOrder().getCommune().getName()+", " + order.getAddressOrder().getDistrict().getName()+", " + order.getAddressOrder().getCommune().getName());
//        address.setPhone(order.getAddressOrder().getPhoneNumber());
//        payerInfo.setShippingAddress(address);
//        payer.setPayerInfo(payerInfo);
        Payment payment = new Payment();
        payment.setIntent(intent.toString());
        payment.setPayer(payer);
        payment.setTransactions(transactions);
        RedirectUrls redirectUrls = new RedirectUrls();
        redirectUrls.setCancelUrl(cancelUrl);
        redirectUrls.setReturnUrl(successUrl);
        payment.setRedirectUrls(redirectUrls);
        apiContext=paypalConfig.apiContext();
        return payment.create(apiContext);
    }

    public Payment executePayment(String paymentId, String payerId) throws PayPalRESTException{
        Payment payment = new Payment();
        payment.setId(paymentId);
        PaymentExecution paymentExecute = new PaymentExecution();
        paymentExecute.setPayerId(payerId);
        apiContext=paypalConfig.apiContext();
        return payment.execute(apiContext, paymentExecute);
    }

}
