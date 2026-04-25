[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/aJfDxjD8)

## Current Implementation & Offline Logic
I chose to use a userId field instead of a full local user table. This avoids storing sensitive personal data on the device.

Even though we plan to use Keycloak, I included the userId to handle cases where multiple users might use the same device offline. This makes it possible to differenciate the activities to the right user. For now, this ID is a mock value and will be changed to the Keycloack ID.