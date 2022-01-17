# TeleFix

## Application Description

### Motivation & Context

People get into troubles at least once when having their motorbike broken in the middle of the street. This will lead to some unwanted scenarios such as:

- Walking vehicles for a long distance
- Unable to find the nearest repairing store/garage or have no one to ask for help
- Uncertain about a garage's service quality & prices

Moreover, in the process of vehicles maintenance, some inconveniences may occur such as: 

- Waiting for too long due to impulsive priority of garages
- Not flexible in choosing service providers when only some large-scale garages have their records saved

### Solution

**TeleFix** is an Android mobile application aiming to help motorcycle owners to find assistance when their vehices malfunction, as well as having a more convenient experience in vehicle's maintenance process.

The application serves 2 major types of users: Customer or Vehicle Owner and Vendor or Mechanic

All `Customer` can register for an account including both basic contact info and their vehicles info. The application will allow them to:

- Sending SOS requests whenever their motorbike is broken
- Booking maintenance time slot at certain garages

For `Mechanic`, their account will be bound to a registered vendor. The application will allow them to:

- Handle SOS Request from Customer
- Allocate themselves to maintenance appointments and keep track of assigned appointments

The application has fully covered the login, registration, and update information process for both type of users.

### Business Context Assumption

- The vendor must register for their business outside the application, and their `mechanic` need to submit their vendor ID during registration.

- The process of in-app information validation has not been implemented yet.

- All in-app exising vendors provide maintenace services.

- Payment will be processed by offline agreement between users and mechanics and the confirmation of payment will be sent to the system's database.

- For `Mechanic`, since they are bound with a vendor, there should be an admin dashboard for that vendor to manage their business data. The application for `Mechanic` is only for task allocation.

### Value Propositions

- For customer using the app, they can receive real-time support with money transparency and time saving.

- For service providers, especially small & medium garage, they can reach more customer and have an additional management channel to manage their business.

## Members

- Le Nguyen Truong An - s3820098
- Nguyen Bao Khang - s3817970
- Pham Cong Minh - s3818102

## Work Distribution

- **Data Crawling and Population**: `Khang Nguyen`
- **Database Design and Implementation**: `Minh Pham` & `Khang Nguyen`
- **Overall Interfaces Design**: `An Le`
- **User Account Services**: `An Le`
- **SOS Request Service**: `Minh Pham`
- **Maintenance Request Service**: `Khang Nguyen`

## Status

- Location: **_Ho Chi Minh, Vietnam_**
- Last Updated: **_Sunday, Jan 15, 2022 23:59:59 (GMT+7)_**

## Demo Link

Please find this link to see the demo: [TeleFix Demo](OneDriveLinkHere) (**_Only inside RMIT_**)

## Fulfilled Functionalities

- Holistic registration and management of customer and mechanic account including login, registration, update information
- End-to-end SOS request process updating in real-time with notification
- Maintenance appointment booking and management including interaction from both sides and target the right mechanic from the requested vendor

## Unfulfilled Functionalities

Due to the lack of time and human resources, the following features are not implemented yet:

- `Mechanic` can't perform post-processing of the maintenance request (Eg. issue bill, send note or reminder to the customer)
- Validate if user's email is real
- `Customer` can only keep track of completed SOS requests, not pending requests or getting reminders
- Time picker for maintenance appointment booking haven't been aligned with the vendors' Open - Close time
- History tracking can only shown the records timestamp, type, and status, not including detailed information (Eg. billing, note) when clicked
- History tracking should be able to filter by date, registered vehicles

## Known Bugs, Open Issues and Notes

1. Note: run the application on **Google Pixel 3a Emulator with Android API 28** that supports Google Services to enable Google Maps features and for best UI/UX experience.

2. Note: set **the emulator's location** before hitting `Start` on application's splash screeen to ensure further map-related services are served correctly when in-app. If they are not, please hit the `Refresh` button at least twice to locate to your chosen location. The idle location has latitude of 10.784052457755285, longitude of 106.69699154298507 which is "Pizza 4P's Hai Ba Trung, 151b Hai Bà Trưng, Phường 6, Quận 3, Thành phố Hồ Chí Minh 700000".

3. Vendor's locations are not completely accurate due to real-time data crawling process.

4. If `UI is not responding` or any no-error auto turn-off happens on the first time running, just re-run the app.

5. Note: most of bottom dialogs will not be dismissed by clicking out on the screen, please use `x` icon to dismiss them.

## Technology Used

The application was developed natively in `Java` with `Android Studio` as IDE. In order to develop this application fast and secured, these `Google Firebase Platform` features are used:

- `Authentication`: For login, registration, and update information process

- `Firestore Storage`: Connected with Authentication to store user's information. Storing static data such as vehicle's information, vendor's information, and completed records

- `Realtime Database`: For handling both type of requests with real-time response

- `Google Maps API` is used to develop the application's map feature with customized marker. Supported library such as `Geocoder` and built-in `Google Map` are used to get display convert between address and coordinates to support location navigation

- `Notification Service` is also used to support the SOS request feature and enhance the user experience

## License

2021 GitHub, Inc. © TeleFix
