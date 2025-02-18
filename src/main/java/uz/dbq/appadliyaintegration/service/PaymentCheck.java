package uz.dbq.appadliyaintegration.service;


public interface PaymentCheck {

    Double payment_v1(String g9inn);

    Double payment_v3(String g9inn);

    Double payment_v5(String g9inn);

    Double payment_v6(String g9inn);

    boolean big_organization(String g9inn);

    boolean big_organization_mat(String g9inn);

}
