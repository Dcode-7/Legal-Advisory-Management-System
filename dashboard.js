// dashboard.js

// Wait for the document to load before attaching event listeners
document.addEventListener("DOMContentLoaded", function () {
    // Get the "Edit Profile" button by its ID
    const editProfileButton = document.getElementById("editProfileBtn");

    // Add event listener to the "Edit Profile" button
    if (editProfileButton) {
        editProfileButton.addEventListener("click", function () {
            // Redirect to form.html when the button is clicked
            window.location.href = "form.html";
        });
    }

    // Call getUserProfile to load user data from the API after the page has loaded
    getUserProfile();
});

// Function to fetch user profile data and update the UI
async function getUserProfile() {
    const token = sessionStorage.getItem("authToken"); // Retrieve the token from sessionStorage

    if (!token) {
        console.log("No token found. Please log in.");
        return;
    }

    try {
        // Make the API call using the token in the Authorization header
        const response = await fetch("http://localhost:8081/user/details", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`, // Include the token in the Authorization header
                "Content-Type": "application/json",
            },
        });

        // Parse the response from the API
        if (response.ok) {
            const userData = await response.json();
            console.log("User profile:", userData);
            // Update the profile UI with fetched data
            updateProfileUI(userData);
        } else {
            console.log("User not found, displaying default values.");
            // If the user is not found, display default profile information
            displayDefaultProfile();
        }
    } catch (error) {
        console.error("Error fetching profile:", error);
        // Handle error scenario, display default profile as a fallback
        displayDefaultProfile();
    }
}

// Function to update the profile UI with the fetched data
function updateProfileUI(userData) {
    // Assuming the response contains user details in a structure like:
    // { clientID, name, email, address, contactNo, occupation }
    const userProfileSection = document.querySelector(".card-body");

    if (userData) {
        // Update the profile information dynamically with the data from the API
        userProfileSection.querySelector("h5").innerText = userData.name || "John Doe"; // Name
        userProfileSection.querySelector(".list-group-item:nth-child(1)").innerHTML = `<strong>Client ID:</strong> ${userData.clientID || "Not Available"}`;
        userProfileSection.querySelector(".list-group-item:nth-child(2)").innerHTML = `<strong>Email:</strong> ${userData.email || "Not Available"}`;
        userProfileSection.querySelector(".list-group-item:nth-child(3)").innerHTML = `<strong>Address:</strong> ${userData.address || "Not Available"}`;
        userProfileSection.querySelector(".list-group-item:nth-child(4)").innerHTML = `<strong>Contact no:</strong> ${userData.contactNo || "Not Available"}`;
        userProfileSection.querySelector(".list-group-item:nth-child(5)").innerHTML = `<strong>Occupation:</strong> ${userData.occupation || "Not Available"}`;
    }
}

// Function to display default profile when user is not found or error occurs
function displayDefaultProfile() {
    const userProfileSection = document.querySelector(".card-body");

    // Displaying default values in case the user is not found
    userProfileSection.querySelector("h5").innerText = "John Doe"; // Default Name
    userProfileSection.querySelector(".list-group-item:nth-child(1)").innerHTML = "<strong>Client ID:</strong> Not Available";
    userProfileSection.querySelector(".list-group-item:nth-child(2)").innerHTML = "<strong>Email:</strong> Not Available";
    userProfileSection.querySelector(".list-group-item:nth-child(3)").innerHTML = "<strong>Address:</strong> Not Available";
    userProfileSection.querySelector(".list-group-item:nth-child(4)").innerHTML = "<strong>Contact no:</strong> Not Available";
    userProfileSection.querySelector(".list-group-item:nth-child(5)").innerHTML = "<strong>Occupation:</strong> Not Available";
}

document.getElementById('logoutBtn').addEventListener('click', function() {
    // Clear session storage or any other authentication data if necessary
    // sessionStorage.clear();  // Uncomment this line if you're using session storage for authentication
    
    // Redirect to index.html after logout
    window.location.href = 'index.html'; // or 'index.html' depending on your file structure
  });
  
