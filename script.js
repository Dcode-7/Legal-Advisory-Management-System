// Select the form element
const signupForm = document.getElementById("signupForm");

// Listen for form submission
signupForm.addEventListener("submit", async (event) => {
  event.preventDefault(); // Prevent the default form submission

  // Collect form data
  const formData = {
    email: document.getElementById("email").value,
    password: document.getElementById("password").value,
    role: document.querySelector("input[name='role']:checked").value,
  };

  try {
    // Send data to the server using Fetch API with CORS and credentials included
    const response = await fetch("http://localhost:8081/user/signup", {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
      },
      body: JSON.stringify(formData),
      mode: "cors", // Ensure CORS handling
      credentials: "include", // Include credentials if the backend expects cookies or session info
    });

    // Parse the response as text
    const responseText = await response.text();

    // Handle the response based on the status code
    if (response.ok) {
      // If the request was successful, show a success message
      displayMessage("Successfully Registered!", "success", "signupResponse");
    } else {
      // If the email already exists or there was invalid data, show an error message
      displayMessage(responseText, "error", "signupResponse");
    }
  } catch (error) {
    console.error("Error:", error);
    displayMessage("Something went wrong. Please try again later.", "error", "signupResponse");
  }
});

// Reusable function to display messages
function displayMessage(message, type, elementId) {
  const responseElement = document.getElementById(elementId);
  if (responseElement) {
    responseElement.innerText = message;
    responseElement.className = type === "success" ? "text-success" : "text-danger";
    responseElement.style.textAlign = "center";
  } else {
    console.error("Element with id '" + elementId + "' not found.");
  }
}
