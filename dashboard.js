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
});
