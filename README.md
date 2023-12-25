# Getting Started with Magento 2 Mobile App Builder

## Basic Information
**Magento 2 Mobile App Builder** can transform your Magento 2 website to a fully funtional Android app. It will provide the customers with lots of features, to relish their shopping experience in a more easy and mobile way in order to provide Ubiquity (Easier information access in real-time), Convenience (Devices that store data are always at hand), Accessibility (Choice to limit the accessibility to particular persons who can be contacted anywhere anytime), Personalization (Creating services that customize the end-user experience), and Localization (Matching services to the location of the customers), which is just some taps and swipes away. The working demo of the app can be found on [Play Store](https://play.google.com/store/apps/details?id=com.webkul.magento2.mobikul).

## Supported Version
:star:	`Target Sdk Version: 28`

:star:	`Minimum Sdk Version: 17`

## Instructions To Configure The Application
1. First, Install the Mobikul API Module (If not installed yet) on your server.

2. Now, You need to create a username and password for the app to communicate with the server. For that, You need to open your Magento's Admin panel and goto Store >> Configuration >> Webkul >> Mobikul. Inside this tab you would have to fill two fields Username and Passsword.

3. After setting the Username and Password, You need to update that in app code also. Mobikul source code contains a file called ApplicationConstants which contains all the neccessary information to run the app. There you will find three variable which are used to connect the app with the server.

4. Now you need to set some constants in the app code.
   - Open the `ApplicationConstants.kt` class in your Android Studio.
   - There you will find three contants that needs to changed.
     - **BASE_URL** - It is the Domain name or the IP of your server
        ```
        String BASE_URL = "https://example.com"
        ```
     - **API_USER_NAME** - Username created in admin panel.
        ```
        String API_USER_NAME = "your_username"
        ```
     - **API_PASSWORD** - Password created in admin panel.
        ```
        String API_PASSWORD = "your_password"
        ```

5. Now, You will be able to connect to the server with your app.

## Instructions To Configure The Application Images
#### 1. Splash screen-
- **Path** - /mobikul/src/main/res/drawable/
- **Name** - splash_screen.png

#### 2. Placeholder-
- **Path** - /mobikul/src/main/res/drawable/
- **Name** - placeholder.xml

#### 3. Launcher icon-
- **Path** - /mobikul/src/main/res/mipmap-anydpi-v26,hdpi,mdpi,xhdpi,xxhdpi,xxxhdpi
- **Name** - ic_launcher

#### 4. Status bar icon
- **Path** - /mobikul/src/main/res/drawable
- **Name** - ic_app.xml

## Push notification
For push notification you need to change google-services.json file with yours.
- **Path** - /mobikul/google-services.json

>Change `applicationId` in build.gradle file with the package-name in google-services file.

## Color Code
For changing the color theme of the application go to color.xml file and hange the colors.
- **Path** - /mobikul/src/main/res/values/color.xml

## App name
Change application name from mobikul/src/main/res/values/strings.xml
- app_name
- new_version_available

## For Add New Localization:
Goto /mobikul/src/main/res/value/ and find string.xml file and translate it according to your localization.

#### Sample
 1. English
    ```
    <string name="new_version_available">There is a newer version of Mobikul application available, click UPDATE to upgrade now?</string>
    ```
 2. Arabic
    ```
    <string name="new_version_available">هل هناك إصدار أحدث من تطبيق موبيكول المتاحة، انقر فوق موافق للترقية الآن؟</string>
    ```

## Magento 2 Mobile App-Admin-End Configuration:
   
   Follow the below link to configure home page.
 
[Knowledge base](https://mobikul.com/knowledgebase/magento2-mobile-app-admin-end-configuration/)


    
