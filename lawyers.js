// Define the API URLs for both getAllLawyers and getLawyersBySpecialization
const API_URL_GET_ALL_LAWYERS = 'http://localhost:8081/user/search'; // Replace with your actual API endpoint
const API_URL_FILTER_LAWYERS = 'http://localhost:8081/user/filterLawyersBySpecialization'; // Replace with your actual filter API endpoint

// Select the elements from the HTML
const lawyersTableBody = document.querySelector('table tbody'); // The tbody where lawyer data will be inserted
const specializationSelect = document.getElementById('inlineFormCustomSelect'); // The specialization dropdown

// Function to retrieve the token from sessionStorage (or wherever it's stored)
function getAuthToken() {
  return sessionStorage.getItem('authToken'); // Get the token from sessionStorage
}

// Function to fetch all lawyers after the user is authenticated
async function fetchLawyers() {
  const token = getAuthToken(); // Retrieve the token

  if (!token) {
    alert("Please log in to view lawyers.");
    return;
  }

  try {
    const response = await fetch(API_URL_GET_ALL_LAWYERS, {
      method: "GET",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`, // Attach the token in the Authorization header
      },
      credentials: "include", // Include credentials (cookies/session info) for authorized requests
    });

    const data = await response.json();

    if (response.ok) {
      // Populate the table with the lawyers' data
      populateLawyersTable(data);
    } else {
      console.error('Failed to fetch lawyers:', data);
      alert('Could not fetch lawyers at this time. Please try again later.');
    }
  } catch (error) {
    console.error('Error fetching lawyers:', error);
    alert('Something went wrong while fetching lawyers.');
  }
}

// Function to fetch lawyers by specialization
async function fetchLawyersBySpecialization(specialization) {
  const token = getAuthToken(); // Retrieve the token

  if (!token) {
    alert("Please log in to view filtered lawyers.");
    return;
  }

  try {
    const response = await fetch(`${API_URL_FILTER_LAWYERS}?specialization=${specialization}`, {
      method: "GET",
      headers: {
        "Content-Type": "application/json", // For GET, content type is not critical, but let's keep it
        "Authorization": `Bearer ${token}`, // Attach the token in the Authorization header
      },
      credentials: "include", // Include credentials (cookies/session info) for authorized requests
    });

    const data = await response.json();

    if (response.ok) {
      // Populate the table with the filtered lawyers' data
      populateLawyersTable(data);
    } else {
      console.error('Failed to fetch filtered lawyers:', data);
      alert('Could not fetch lawyers by specialization. Please try again later.');
    }
  } catch (error) {
    console.error('Error fetching filtered lawyers:', error);
    alert('Something went wrong while fetching filtered lawyers.');
  }
}

// Function to populate the lawyers' table
function populateLawyersTable(lawyers) {
  // Clear the existing table rows
  lawyersTableBody.innerHTML = '';

  // Populate the table with the lawyers' data
  lawyers.forEach(lawyer => {
    const row = document.createElement('tr');

    row.innerHTML = `
      <td><input type="checkbox" name="lawyer" value="${lawyer.lawyerID}" /></td>
      <th scope="row">${lawyer.lawyerID}</th>
      <td>${lawyer.name}</td>
      <td>${lawyer.contactNo}</td>
      <td>${lawyer.specialization}</td>
      <td>${lawyer.fees}</td>
      <td>${lawyer.rating}</td>
    `;

    lawyersTableBody.appendChild(row);
  });

  // Now that the table is populated, add the event listener for checkboxes
  const lawyerCheckboxes = document.querySelectorAll('input[name="lawyer"]');
  
  // Ensure only one checkbox can be selected at a time
  lawyerCheckboxes.forEach(function(checkbox) {
    checkbox.addEventListener('change', function() {
      // Uncheck all other checkboxes when one is selected
      lawyerCheckboxes.forEach(function(otherCheckbox) {
        if (otherCheckbox !== checkbox) {
          otherCheckbox.checked = false;
        }
      });
    });
  });
}

// Event listener for specialization dropdown change
specializationSelect.addEventListener('change', (event) => {
  const selectedSpecialization = event.target.value;

  // If "Choose..." is selected (default option), fetch all lawyers
  if (selectedSpecialization === "Choose...") {
    fetchLawyers(); // Fetch all lawyers
  } else {
    // Otherwise, filter lawyers based on the selected specialization
    fetchLawyersBySpecialization(selectedSpecialization);
  }
});

// Fetch all lawyers when the page loads (default behavior)
document.addEventListener('DOMContentLoaded', () => {
  // Add the "Choose..." option to the select dropdown
  const chooseOption = document.createElement('option');
  chooseOption.textContent = 'Choose...';
  chooseOption.selected = true;
  specializationSelect.appendChild(chooseOption);

  // Pre-load the table with all lawyers
  fetchLawyers(); // Initially load all lawyers
});

// Function to handle case submission
async function submitCase(lawyerID, caseDescription) {
  const token = getAuthToken(); // Retrieve the token

  if (!token) {
    alert("Please log in to raise a case.");
    return;
  }

  // Prepare the data to send
  const requestData = {
    lawyerId: lawyerID,
    caseDescription: caseDescription,
    status: "OPEN" // You can choose to set this as default, or get it from another form input
  };

  try {
    // Send the POST request to raise a case
    const response = await fetch('http://localhost:8081/user/raise-case', {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "Authorization": `Bearer ${token}`,
      },
      body: JSON.stringify(requestData), // Sending case data as JSON in the request body
    });

    const data = await response.json();

    if (response.ok) {
      alert('Case successfully raised!');
      console.log(data);
    } else {
      console.error('Failed to raise case:', data);
      alert('Could not raise the case. Please try again later.');
    }
  } catch (error) {
    console.error('Error raising case:', error);
    alert('Something went wrong while raising the case.');
  }
}

// Event listener for the case submission
document.getElementById('submitCaseButton').addEventListener('click', function () {
  const selectedCheckbox = document.querySelector('input[name="lawyer"]:checked'); // Get selected checkbox
  const caseDescription = document.getElementById('caseDescription').value.trim(); // Get case description

  // Check if a lawyer is selected and case description is provided
  if (!selectedCheckbox || !caseDescription) {
    alert("Please select a lawyer and provide a case description.");
    return;
  }

  const selectedLawyerID = selectedCheckbox.value; // Get the ID of the selected lawyer

  // Submit the case
  submitCase(selectedLawyerID, caseDescription);
});
