package com.example.hemocares.service;

public class BaseURL {
//    public static String baseUrl = "http://192.168.43.100:5060/";
//    public static String baseUrl = "http://192.168.4.250:5060/";
//    public static String baseUrl = "http://192.168.1.8:5060/";
    public static String baseUrl = "http://studio81.co.id:5060/";

    public static String login = baseUrl + "user/login";
    public static String register = baseUrl + "user/create";

    public static String updateUser = baseUrl + "user/";
    public static String updateUserStatus = baseUrl + "user/updateStatus/";
    public static String getUserById = baseUrl + "user/getById/";
    public static String getUserByStatus = baseUrl + "user/getByStatus/";
    public static String getUserByStatusAll = baseUrl + "user/getByStatusAll/";
    public static String getCountBlood = baseUrl + "user/getCount";

    public static String postLocation = baseUrl + "location/create";
    public static String getLocation = baseUrl + "location/getLocation";
    public static String updateLocation = baseUrl + "location/updateLocation/";

    public static String postReport = baseUrl + "report/create";
    public static String getReportAll = baseUrl + "report/getReportAll/";
    public static String getReportAllDetail = baseUrl + "report/getReportAllDetail/";

    public static String showUser = baseUrl + "access/getdataUser";
    public static String completeUser = baseUrl + "access/completeData/";
}
