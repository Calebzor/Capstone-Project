#!/bin/bash

echo $GOOGLE_SERVICES | base64 --decode > ~/$CIRCLE_PROJECT_REPONAME/app/google-services.json