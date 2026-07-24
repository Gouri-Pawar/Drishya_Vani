# DrishyaVani

**Location-Based Voice Narration Mobile Application**

DrishyaVani is a smart, voice-first Android app that helps people — especially tourists new to a city and visually impaired users — understand where they are and what's around them. It detects the user's real-time location, narrates history/culture/place information out loud, shows nearby attractions, and combines navigation, travel information, and personal safety into a single app.

## Problem It Solves

- People often get lost while traveling, especially in unfamiliar cities.
- New visitors have no easy way to learn about nearby places, history, or culture.
- Visually impaired people can't see their surroundings and depend on others for navigation and information — most existing apps (like Google Maps) focus purely on directions, not spoken guidance or context.

DrishyaVani addresses this with a single Android app that speaks location context out loud, in the user's preferred language, and adds emergency safety features on top.

## Key Features

- **Live location detection** — uses the Fused Location Provider API to get real-time latitude/longitude, with a homepage that shows the user's current location
- **Voice narration** — a button on the home screen speaks out information about the current place using Android Text-to-Speech (TTS)
- **Place information** — fetches real place info from the Wikipedia REST API, with images pulled from the Unsplash API (displayed via Picasso)
- **Nearby places** — uses the OpenStreetMap Overpass API to find nearby schools, hospitals, restaurants, and other points of interest based on the user's coordinates
- **Multi-language support** — English, Hindi, and Marathi, powered by Google ML Kit Translation, so narrated place info is translated into the user's chosen language
- **Google Maps integration** — opens Google Maps via an explicit `ACTION_VIEW` intent to show current location, nearby places, and directions
- **Travel history** — logs every visited place with its date in Firebase Firestore
- **Favourite places** — users can save places to favourites (with name, distance, and type), stored in and fetched from Firestore
- **Emergency safety module**
  - One-tap dial to police / ambulance / SOS via a dial intent
  - Share live location via message
  - Save and call personal emergency (family) contacts, stored in and fetched from Firestore

## System Architecture

- **User** — gives current location / interacts with the app and receives audio guidance
- **Android Mobile App** — detects location, sends API requests, and processes/controls all features
- **Location Services (GPS & Google Maps)** — provide real-time latitude/longitude and show maps/directions
- **External APIs**
  - Wikipedia API → place information
  - Unsplash API → place images
  - OpenStreetMap Overpass API → nearby places
- **Firebase Firestore** — stores visited places, favourites, and emergency contacts
- **Text-to-Speech Engine** — converts fetched information into voice narration

## Tech Stack

- **Platform:** Android (Java, Gradle)
- **Auth & Database:** Firebase Authentication + Cloud Firestore
- **Location:** Fused Location Provider API, GPS, Google Maps (explicit intent)
- **APIs:** Wikipedia REST API, Unsplash API, OpenStreetMap Overpass API
- **Voice:** Android Text-to-Speech (TTS)
- **Translation:** Google ML Kit Translation (English / Hindi / Marathi)
- **Image loading:** Picasso

## App Modules / Activities

- **Main Activity** — current location, quick access to visited places, emergency contacts, language selection, etc.
- <img width="738" height="1600" alt="WhatsApp Image 2026-07-24 at 3 38 22 PM" src="https://github.com/user-attachments/assets/4be52174-6fc5-40bd-bc33-e2e5b09b2140" />

- **Select Language Activity** — choose preferred language; translates place info via ML Kit
- <img width="738" height="1600" alt="WhatsApp Image 2026-07-24 at 3 38 24 PM" src="https://github.com/user-attachments/assets/e5579c13-a674-44ea-ae0b-5adf1fab4890" />

- **Travel History Activity** — list of visited places with dates (from Firestore)
- <img width="738" height="1600" alt="WhatsApp Image 2026-07-24 at 3 38 25 PM" src="https://github.com/user-attachments/assets/0c11bb91-b6ae-420c-b747-d3e53f7689b9" />

- **Nearby Places Activity** — nearby locations via Overpass API; add to favourites
<img width="738" height="1600" alt="WhatsApp Image 2026-07-24 at 3 38 25 PM (1)" src="https://github.com/user-attachments/assets/643a201f-f77c-4055-bd9c-1c899616dd75" />


- **Favourite Places Activity** — saved favourite places with name, distance, and type
- <img width="738" height="1600" alt="WhatsApp Image 2026-07-24 at 3 38 26 PM" src="https://github.com/user-attachments/assets/82e4e53d-1919-4621-a000-2d1e71b513af" />

- **Map Activity** — current location and nearby places on Google Maps
- <img width="738" height="1600" alt="WhatsApp Image 2026-07-24 at 3 38 23 PM (1)" src="https://github.com/user-attachments/assets/71d0f239-c969-4e5b-a3ac-b0be5c73c27f" />

- **Emergency Activity** — dial police/ambulance/SOS, share live location, manage & call emergency contacts
- <img width="738" height="1600" alt="WhatsApp Image 2026-07-24 at 3 38 27 PM" src="https://github.com/user-attachments/assets/aa5f0ad3-ec5b-418c-adf1-85031f6ffc96" />


## Advantages

- Simple, voice-based navigation and guidance
- Genuinely useful for visually impaired users, not just a convenience feature
- Real-time location info combined with historical/cultural context
- Everything — navigation, information, and safety — in one app
- Tracks visited and favourite places over time


**Gouri Pawar**
Department of Computer Science & Engineering
