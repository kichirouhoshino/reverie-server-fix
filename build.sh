#!/bin/bash
# A simple build script for the Reverie Server Fix mod

echo "Building the mod using Gradle..."
./gradlew build

if [ $? -eq 0 ]; then
    echo "======================================"
    echo "Build Successful!"
    echo "======================================"
    
    # Optional: Copy to a clear output folder
    mkdir -p releases
    cp build/libs/*-1.0.0.jar releases/
    
    echo "Your compiled jar is located at:"
    ls -l releases/*-1.0.0.jar
else
    echo "Build failed! Please check the output above."
fi
