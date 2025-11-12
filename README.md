# CSE 476 – ApartmentRater

## Overview
ApartmentRater is a full Android + Django client/server application that lets users review and favorite apartment complexes.  
The Android client communicates with a Django REST backend hosted on Google Cloud, using secure token authentication and JSON over HTTP.

---

## Features
-  View apartments on a map via Google Places API  
-  Submit and store reviews for each apartment  
-  Local storage using SharedPreferences for saved reviews and favorites  
-  Remote database sync with Django ORM  
-  Secure authentication using token headers  
-  Error handling with user-friendly Toast notifications  

---

## Architecture Summary

### Client (Android)
- Built in **Java** using **Android Studio**
- Uses **Retrofit** for client–server HTTP communication
- Local data saved via `SharedPreferences` for:
  - Offline review persistence
  - Favorite apartment tracking
- Displays map markers from **Google Maps / Places API**
- Handles token authentication and network errors gracefully

### Server (Django)
- Hosted on a Google Cloud VM (`136.115.50.113`)
- REST API built with **Django REST Framework**
- Uses SQLite (persistent remote DB)
- Endpoints (examples):



- Models:
- **Apartment:** stores `google_place_id`, `name`, `address`
- **Review:** linked to Apartment + User, stores comment & timestamps
- **Profile:** user bio + optional info
- Authentication: Django Token Authentication

---

## Admin Access
- **URL:** http://136.115.50.113:8000/admin/
- above is the GCP production server. 
- **Username:** `admin`  
- **Password:** `admin`

### Admin Features
- View all Apartments and Reviews in database
- Manually add or remove entries
- Verify that reviews from the Android app are successfully synced to the backend
