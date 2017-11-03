#!/bin/bash

echo $GOOGLE_SERVICES | services.jsonbase64 --decode > ~/$CIRCLE_PROJECT_REPONAME/app/google-