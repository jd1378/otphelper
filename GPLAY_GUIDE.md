# How to obtain `SERVICE_ACCOUNT_JSON` for CI/CD

in play console go to "Setup > API access" and accept terms. create a Google Cloud project.

Under "Service accounts" press on "Learn how to create service accounts."

In the dialogue box, follow the link to Google Cloud platform and login with the same ID you have used in Play Developer Console.

Make sure you’re on the correct project in Google Cloud Platform. If not, select the right project from the drop-down at the top.

Click on Create Service Account at the top.

Provide a service account name for the same. (doesn't matter, It's a display name). press "Create and continue".

in next step:

Click on Role and scroll down to Service Accounts and select Service Account User role from the drop-down.

Click continue and done

on the newly created account press 3 dots menu and press "Manage keys"

Add Key -> Create new Key -> JSON

re-visit Google Play Console -> Setup -> API access -> under "Service accounts" you should see this account now.

Click on View Play Console Permissions of the newly created account.

Grant permissions your want this user to have.

Done.

with help from: <https://help.graphy.com/hc/en-us/articles/6953161090205-Android-How-to-create-Service-Account-json-file-required-to-update-android-app-on-Google-Play-Console->
