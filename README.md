# AI_agent
EnhancedAIWebAgent is a Java application designed to make your tasks easier by connecting with Google Sheets, using AI-powered web searches, and saving the results neatly into a CSV file stored in Google Cloud Storage.

FEATURES:-
1.Seamless Google Sheets Integration
  -Easily fetch data from specific columns in your Google Sheet to work with.
2.AI-Powered Web Search
  -Uses OpenAI's GPT model to perform web searches for provided queries.
3.Google Cloud Storage
  -Saves the search results in a CSV file and uploads them to a designated cloud storage bucket.

REQUIREMENTS:-
1.Java 8 or later
2.Google Cloud SDK
  -Install and configure the SDK with credentials for accessing Google Sheets and Cloud Storage.
3.OpenAI API Key
  -You need a valid API key for accessing OpenAI's services.
4.Libraries
  ->Include the following dependencies:
   -Google API Client Libraries for Sheets and Cloud Storage
   -OkHttp (for HTTP requests)
   -JSON library (e.g., org.json)

INSTALLATION STEPS:-
1.Clone or download this repository.
2.Configure the required credentials:
  ->Place the service-account-key.json file (Google Cloud credentials) in your project directory and update the GOOGLE_CREDENTIALS_FILE variable.
  ->Replace OPENAI_API_KEY with your actual OpenAI API key.   #As this will be exposure for all thatswhy i have not provided it.
3.Update the following placeholders in the code:
  ->your-spreadsheet-id with the ID of your Google Sheet.
   ->Sheet1!A:B with the desired range of cells to read.
   ->ai-web-agent-project with your Google Cloud Storage bucket name.

   
File Upload to Google Cloud Storage:-
Once the results are processed, they’re saved in a CSV file and automatically uploaded to your chosen Cloud Storage bucket. After the upload is complete, the file path will be displayed in the console for easy access.


Error Handling:-
If something goes wrong while reading data from Google Sheets or interacting with the OpenAI API, the issue is logged, and a default error message is added to the results to keep things running smoothly. Make sure your credentials are set up correctly to avoid any hiccups during execution.
