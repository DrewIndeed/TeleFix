# TeleFix

## Application Description

### Motivation & Context

People get into troubles at least once when having their bike broken in the middle of the street. These will leads to some troublesome scenario such as:

- Walk vehicles for a long distance
- Cannot find the nearest repairing store/garage or have no one to ask for help
- Uncertain about service quality & price

For vehicles maintenance process, some inconveniences may occur such as: waiting for too long or not freely choosing service providers but need to centralized to some big garages to have their records saved.

### Solution

TeleFix is a mobile application to help motorcycle's owner to find help whenever their bike is broken, as well as having more convenient experience in vehicle's maintenance process.

The application contains 2 user interfaces: for Customer (Vehicle Owner) and for Vendor (Mechanic).

All the `Customer` can register for an account (including contact information and vehicles). The application will cover 2 main features:

- Send SOS request whenever their bike is broken
- Booking a maintenance appointment to secure their time slot

For the `Mechanic`, their account will be sticked to a registered vendor. The application will cover 2 main features:

- Handle SOS Request from Customer
- Allocate themselves to maintenance appointment bookings and keep track of assigned appointments

The application has fully covered the login, registration, and update information process for both type of users.

### Business Context Assumption

- The vendor must register for their business outside the application, and their `mechanic` need to submit their vendor ID during registration.

- The process of in-app information verification has not been implemented yet.

- The payment process will be assumed to process offline, which means they will make agreement and only send the confirmation of payment to the database.

- For `Mechanic`, since they are sticked to a vendor, there should be an admin dashboard for that vendor to manage their business data. The application for `Mechanic` is only for task allocation.

### Value Propositions

For customer using the app, they can receive real-time support with transparent pricing and time saving.

For service providers, especially small & medium garage, they can reach more customer and have an additional management channel to manage their business.

## Members

- Le Nguyen Truong An - s3820098
- Nguyen Bao Khang - s3817970
- Pham Cong Minh - s3818102

## Work Distribution

- **Database Design and Implementation**: `Minh Pham` & `Khang Nguyen`
- **Data crawling and Population**: `Khang Nguyen`
- **Front-End Overall Design**: `An Le`
- **User (Customer & Mechanic) Service**: `An Le`
- **SOS Request Service**: `Minh Pham`
- **Maintenance Request Service**: `Khang Nguyen`

## Status

- Location: **_Ho Chi Minh, Vietnam_**
- Last Updated: **_Sunday, Jan 15, 2022 23:59:59 (GMT+7)_**

## Demo Link

Please find this link to see the demo: [TeleFix Demo](OneDriveLinkHere) (**_Only inside RMIT_**)

## Fulfilled Functionalities

- Holistic registration and management of customer and mechanic account (including login, registration, update information)
- End-to-end SOS request process (detailed progress updating in real-time with notification)
- Maintenance appointment booking process and management (interaction from both sides and target the right mechanic from the requested vendor)

## Unfulfilled Functionalities

Due to the lack of time and human resources, the following features are not implemented yet:

- Time picker for maintenance appointment booking haven't been aligned with the Open - Close time.
- Mechanic can't perform post-processing of the maintenance request (Eg. issue bill, send note or reminder to the customer)
- Customers can only keep track of completed SOS requests, not including pending requests or getting reminders.
- History tracking can only shown the records timestamp, type, and status, not including detailed information (Eg. billing, note) when clicked.
- History tracking should be able to filter by date, registered vehicles.

## Known Bugs and Open Issues

For the first time running the application, the location of the device should be set before, otherwise the application would probably crash.

For the best experience, the application should be run on **Google Pixel 3a** with supported Google Services to enable Google Maps feature.

## Technology Used

The application was developed natively in Java with Android Studio. In order to develop this application fast and secured, some `Firebase` features are used:

- `Authentication`: For login, registration, and update information process
- `Firestore Storage`: Connected with Authentication to store user's information. Storing static data such as vehicle's information, vendor's information, and completed records.
- `Realtime Database`: For handling both type of requests with real-time response.

`Google Maps API` is used to develop the application's map feature with customized marker. Supported library such as `Geocoder` and built-in `Google Map` are used to get display convert between address and coordinates to support location navigation.

`Notification Service` is also used to support the SOS request feature and enhance the user experience.

## License

2021 GitHub, Inc. © TeleFix
