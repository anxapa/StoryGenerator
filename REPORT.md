# Project Report

## Challenges I Faced
**Challenge 1: REST API**
Problem: REST API was hard to figure out to implement with the functionality I needed.

Solution: Simply used the API calls without needing to use the HTTP part of REST, but still implemented the REST principles.

Learned: REST is not only limited to HTTP protocols.

&nbsp;

**Challenge 2: Handling errors**
Problem: Errors were sending exceptions to the ClientHandler/ServerConnection, making them not work properly.

Solution: Let the application itself handle errors by passing on the errors to the GUI and letting them deal with it. This lets
us customize the error messages we can send to specify to the user what is currently wrong with the application.

Learned: Error handling can be handled via sending the error messages to the appropriate layer to handle it the most effectively.

## Design Pattern Justifications
**Strategy Pattern:** Needed different AI behaviors (still in-progress)

**Singleton Pattern:** Needed global save and load handler object accross the different classes.

**Observer Pattern:** Needed a way to make UI respond to actions made by the user effectively.

## AI Usage (BE HONEST!)
Used provided Claude example in Canvas

Modified: Changed their GUI code to fit with my program. Changed their client-and-server code to fit with my program.

Verified: Tested with thorough manual testing.

&nbsp;

Asked Gemini how to properly handle errors with Gemini API calls.

Modified: Changed the API return code to appropriately return error messages so that it can be handled by the GUI layer.

Verified: Tested with thorough manual testing.

## Time Spent: ~60 hours
