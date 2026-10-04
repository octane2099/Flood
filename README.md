
# Flood Help - README

This is a development project. Real service configuration is local-only; follow
the setup instructions below before running the app. See
[source and asset review](docs/SOURCE_REVIEW.md) for inherited/third-party material
and [privacy preparation notes](docs/PRIVACY_REVIEW.md) before sharing source.

This is an independent source snapshot of a team project, including work on
Gemini integration and the Android AI chat interface. It does not inherit the
original Git history and has no remote configured at creation. Source references
and third-party notices remain in the source review; a new repository does not
imply sole authorship. Real service configuration is not included.

## Overview

This Android application, Flood Help, combines map functionality, API information acquisition, and user information sharing. It will provide users with comprehensive and timely local information support, enabling them to more effectively find the resources they need in emergency situations and ensure the safety of their families and communities. Efficient information acquisition, easy-to-understand presentation of information, and easy-to-use features must be considered.

## Table of Contents

1. [Features](#features)
2. [Architecture](#architecture)
3. [Installation](#installation)
4. [Setup](#setup)
5. [Usage Guide](#usage-guide)
6. [Accessibility Features](#accessibility-features)
7. [Emergency Features](#emergency-features)
8. [Contact Management](#contact-management)
9. [Mapping and Location Services](#mapping-and-location-services)
10. [Settings and Customization](#settings-and-customization)
11. [Development Guidelines](#development-guidelines)
12. [Testing and Debugging](#testing-and-debugging)
13. [Contributing](#contributing)

## Features

The app offers the following core functionalities:

1. **High Contrast Mode and Accessibility Settings**: Customize the app's visual appearance to support users with visual impairments.
2. **Emergency Help and Alerts**: Provides quick access to emergency contacts and alert notifications.
3. **Contact Management**: Users can manage their contacts efficiently.
4. **Mapping and Geolocation**: Includes mapping functionalities and location-based services.
5. **Customizable User Settings**: Allows the user to adjust preferences for a personalized experience.
6. **Login and User Management**: Supports user login and account management.
7. **Information Sharing with Verified Map Updates**: Share and receive timely location-based updates on traffic, hazards, and events, supported by crowdsourced data verification.
8. **Real-Time Location Updates for Family:**: Share real-time location updates with family members and receive notifications based on location changes.


### Key Technologies Used

- **Android SDK and Android Studio**: Primary tools for app development.
- **Java/Kotlin**: Programming languages used for writing application logic.
- **Firebase**: Backend service for user authentication and real-time database functionalities.
- **Google Maps API**: Provides mapping and location services.
- **Material Design**: Ensures the app's user interface adheres to Android's design standards.

## Architecture

The app follows the Model-View-Controller (MVC) architectural pattern:

1. **Model**: Contains the core data structures and business logic, including user information, emergency contacts, and settings data.
2. **View**: Manages the layout files and user interface components, such as activities and fragments.
3. **Controller**: Handles the interactions between the model and view, ensuring that user input is correctly processed, and the UI is updated accordingly.

### Project Structure

- **app/src/main/java/com/example/myapplication6**  
  - Contains Java/Kotlin classes for managing the app’s logic and data, including:
    - `Accessibility.java`: Manages the accessibility settings screen.
    - `Contacts.java`: Provides functionalities for managing user contacts.
    - `EmergencyHelp.java`: Handles emergency assistance features.
    - `Home.java`: Represents the main home screen of the application.
    - `LoginActivity.java`: Manages user authentication.
    - `Setting.java`: Controls user settings.
    - `Map.java`: Integrates map-based features.
    - `SurvivalTips.java`: Displays emergency survival tips.
    - `Users.java`: Manages user-related data.

- **app/src/main/res/layout**  
  - XML files for defining user interface layouts, including:
    - `activity_accessibility.xml`: Accessibility settings screen layout.
    - `activity_contacts.xml`: Contact management screen layout.
    - `activity_home.xml`: Main home screen layout.
    - `activity_emergency_help.xml`: Layout for emergency assistance.
    - `activity_map.xml`: Layout for the mapping screen.
    - `activity_alert_notification.xml`: Layout for the alert notifications screen, allowing users to manage and view emergency alerts and notifications settings.
    - `activity_login.xml`: Defines the layout for the user login screen, allowing for secure login and authentication of users to access their personalized settings.
    - `activity_map.xml`:  Layout for the mapping screen, which includes features like real-time location tracking, geofencing, and displaying user or family member locations on the map.
    - `activity_privacy.xml`:  Layout for the privacy settings screen, where users can manage data sharing preferences, location permissions, and other privacy-related options.
    - `activity_profile.xml`: Defines the layout for the user profile screen, where users can view and edit their account details, update profile information, and manage preferences.
    - `activity_setting.xml`: Layout for the app settings screen, allowing users to customize various app preferences such as notifications, display options, and account settings.
    - `activity_survival_tips.xml`: Defines the layout for the survival tips screen, where users can access tips and guidelines on how to stay safe in emergency situations.
   
## Installation

### Prerequisites

Ensure you have the following software installed:

1. **Android Studio**: Latest version.
2. **JDK**: JDK 17 for the project's Android Gradle Plugin 8.5.1.
3. **Android SDK**: Installed with Android Studio.
4. **Emulator or Physical Device**: For testing.

### Installation Steps

1. **Clone the Repository**  
   Clone the project repository to your local machine using Git:
   ```
   git clone <YOUR_REPOSITORY_URL>
   ```

2. **Open the Project in Android Studio**  
   Launch Android Studio and open the cloned project by navigating to the project directory.

3. **Sync Gradle Files**  
   Make sure all dependencies in the `build.gradle` file are up to date and synced with Gradle.

4. **Configure local services**
   Complete the setup below. The real `google-services.json` and `secrets.properties`
   are intentionally excluded from version control. The `.example` files contain
   placeholders only.

## Setup

### API Keys and Configurations

1. **Google Maps and optional Google Sign-In configuration**
   Copy `secrets.properties.example` to `secrets.properties` in the project root.
   Set `MAPS_API_KEY` to your own Maps SDK for Android key. Set
   `GOOGLE_WEB_CLIENT_ID` only if Google Sign-In is configured for your project.
   Gradle supplies the Maps manifest placeholder and the `server_client_id` string
   resource. Do not paste real values into source XML or Java files.
   Environment variables with the same names override `secrets.properties`.

2. **Firebase Setup**  
   Configure the Firebase project and connect it with your Android app:
   - Register your own Android app with package `com.example.myapplication6`.
   - Download its real `google-services.json` and place it in `app/`.
   - `app/google-services.json.example` documents the file shape; it is not a
     working Firebase configuration. Do not use it to connect to a service.
   - Configure Realtime Database and its access rules for the features you use.
   - For Gemini, enable Firebase AI Logic and configure App Check in both the
     client and Firebase console. App Check setup is still pending in this codebase.
     See [Firebase's App Check guide](https://firebase.google.com/docs/ai-logic/app-check).

3. **Permissions**  
   Make sure to request the necessary permissions in `AndroidManifest.xml` for features such as:
   - Location access for maps (`ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`).
   - Internet access (`INTERNET` permission).

4. **Local Android SDK**
   Let Android Studio generate `local.properties`, or copy
   `local.properties.example` and set `sdk.dir` to your local Android SDK.

5. **Verify locally**
   ```powershell
   .\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
   ```
   APKs contain the client configuration used at build time and are excluded from
   source sharing. Source cleanup does not remove credentials from existing Git
   commits; see the privacy preparation notes before publishing any history.

## Usage Guide

### Navigating the Application

1. **Home Screen**  
   The home screen serves as the central hub, providing quick access to the main features.
   
2. **Accessing Settings**  
   Navigate to the settings screen via the bottom navigation bar. Here, you can enable accessibility features, adjust notification settings, and customize the app appearance.

3. **Using the Emergency Help Feature**  
   Access emergency help from the menu. This feature provides an interface to request emergency services or notify contacts in case of an emergency.

### Data Persistence

The app uses `SharedPreferences` for storing settings and state changes. Firebase is utilized for real-time data storage and synchronization of user data.

## Accessibility Features

### Enabling High Contrast Mode

High contrast mode can be toggled on/off from the accessibility settings. This mode enhances readability by increasing the contrast between text and background colors.

### Colorblind Modes

Users can choose from multiple colorblind options to adjust the app’s color scheme:
- **Red-Green Blind**
- **Blue-Yellow Blind**
- **Full Color Blind**

### Background Customization

Toggle between black and white background options for improved visibility.

## Emergency Features

### Quick Access to Help

The emergency help feature provides an interface for calling emergency services, sending alerts to contacts, and accessing survival tips.

### Alert Notifications

The app can display alert notifications to ensure important information is delivered promptly.

## Contact Management

### Adding Contacts

Users can add new contacts by navigating to the contacts screen and selecting the "Add" button.

### Editing and Deleting Contacts

Contacts can be modified or removed from the list using the edit and delete options available in the contact management interface.

### Associating Notes with Contacts

Additional information such as notes can be stored with each contact.

## Mapping and Location Services

### Viewing the Map

The mapping functionality integrates Google Maps to display the user's current location and nearby points of interest.

### Navigation

Users can get directions or share their location with emergency contacts.

## Settings and Customization

### User Preferences

Modify application settings such as notifications, themes, and accessibility options.

### Account Management

Users can update their profiles, log out, or reset their settings from the settings screen.

## Development Guidelines

### Code Style

Follow standard Java/Kotlin coding conventions:
- Use meaningful variable names.
- Keep methods short and concise.
- Properly comment code blocks.

### Version Control

Use Git for version control. Each feature or bug fix should be on a separate branch and follow naming conventions like `feature/xyz` or `bugfix/abc`.

### Testing

1. **Unit Testing**: Ensure core functionalities are covered.
2. **UI Testing**: Validate user interface components and user interactions.

## Testing and Debugging

### Testing Tools

- **JUnit** for unit tests.
- **Espresso** for UI testing.

### Debugging Tips

1. **Use Logcat**: For tracking errors and logs.
2. **Breakpoints**: Set breakpoints to step through code.
3. **Emulator vs. Physical Device**: Test on multiple devices to catch platform-specific issues.


## Contributing
1. Fork the project repository.
2. Clone your fork locally.
3. Make your changes and create a pull request.
4. Follow the contribution guidelines provided in `CONTRIBUTING.md`.

