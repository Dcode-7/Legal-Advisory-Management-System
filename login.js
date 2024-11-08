// Select the form element
const loginForm = document.getElementById("loginForm");

// Listen for form submission
loginForm.addEventListener("submit", async (event) => {
  event.preventDefault(); // Prevent the default form submission

  // Collect form data
  const formData = {
    email: document.getElementById("loginEmail").value,
    password: document.getElementById("loginPassword").value,
    role: document.querySelector("input[name='role']:checked").value,
  };

  try {
    // Send data to the server using Fetch API with CORS and credentials included
    const response = await fetch("http://localhost:8081/user/login", {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify(formData),
      mode: "cors", // Ensure CORS handling
      credentials: "include" // Include credentials if the backend expects cookies or session info
    });

    // Parse the response
    const responseData = await response.json(); // Assuming the response is in JSON format

    // Handle the response based on the status code
    if (response.ok) {
      // Store the Bearer token in sessionStorage
      const token = responseData.token; // Assuming the token is returned as 'token' in the response
      sessionStorage.setItem("authToken", token); // Save the token in sessionStorage

      // Display success message
      displayMessage("Login successful!", "success", "loginResponse");

      // Redirect to the details form page
      window.location.href = "dashboard.html"; // Redirect to the dashboard page after successful login
    } else {
      // If the login fails, display an error message
      displayMessage("Invalid credentials or user role.", "error", "loginResponse");
    }
  } catch (error) {
    console.error("Error:", error);
    displayMessage("Something went wrong. Please try again later.", "error", "loginResponse");
  }
});

// Reusable function to display messages in the modal
function displayMessage(message, type, elementId) {
  const responseElement = document.getElementById(elementId);
  responseElement.innerText = message;
  responseElement.className = type === "success" ? "text-success" : "text-danger";
  responseElement.style.textAlign = "center";
}
