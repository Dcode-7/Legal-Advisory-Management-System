// Ensure the script only runs once the page is loaded
document.addEventListener("DOMContentLoaded", function () {
    // Select the form element
    const detailsForm = document.querySelector("form.needs-validation");
  
    // Handle form submission
    detailsForm.addEventListener("submit", async function (event) {
      event.preventDefault(); // Prevent the default form submission
  
      // Collect data from the form
      const formData = {
        name: document.getElementById("validationCustom01").value,
        contactNo: document.getElementById("validationCustom04").value,
        occupation: document.getElementById("validationCustom05").value,
        address: document.getElementById("validationCustom03").value,
      };
  
      // Get the token from sessionStorage
      const token = sessionStorage.getItem("authToken");
  
      if (!token) {
        displayMessage("You must be logged in to update your details.", "error", "formResponse");
        return;
      }
  
      try {
        // Send data to the server using Fetch API with a PUT method
        const response = await fetch("http://localhost:8081/user/update-profile", {
          method: "PUT", // PUT method to update the data
          headers: {
            "Content-Type": "application/json",
            "Authorization": `Bearer ${token}`, // Attach the token in the Authorization header
          },
          body: JSON.stringify(formData),
          mode: "cors", // Ensure CORS handling if needed
          credentials: "include", // Include credentials for session info
        });
  
        // Parse the response
        const responseData = await response.json();
  
        // Handle the response based on the status code
        if (response.ok) {
          // If the update is successful, display a success message
          displayMessage("Client details updated successfully!", "success", "formResponse");
        } else {
          // If there is an error, display the error message from the server
          displayMessage(responseData.message || "Something went wrong. Please try again later.", "error", "formResponse");
        }
      } catch (error) {
        console.error("Error:", error);
        displayMessage("Something went wrong. Please try again later.", "error", "formResponse");
      }
    });
  
    // Function to display success or error messages
    function displayMessage(message, type, elementId) {
      const responseElement = document.getElementById(elementId);
      responseElement.innerText = message;
      responseElement.className = type === "success" ? "text-success" : "text-danger";
      responseElement.style.textAlign = "center";
    }
  });
  