document.addEventListener("DOMContentLoaded", function () {
    // Fetch and display lawyer data when the page loads
    fetchCasesAndLawyers();

    // Add event listener to the delete button
    const deleteButton = document.getElementById("deleteSelectedBtn");
    if (deleteButton) {
        deleteButton.addEventListener("click", function () {
            const selectedCaseIDs = getSelectedCaseIDs();
            if (selectedCaseIDs.length > 0) {
                deleteCases(selectedCaseIDs);
            } else {
                alert("Please select at least one case to delete.");
            }
        });
    }
});

// Function to fetch and display cases and lawyers
async function fetchCasesAndLawyers() {
    const token = sessionStorage.getItem("authToken");

    if (!token) {
        console.log("No token found. Please log in.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8081/user/caseD", {
            method: "GET",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },
        });

        if (response.ok) {
            const data = await response.json();
            console.log("Fetched Cases and Lawyers:", data);
            updateLawyersTable(data);
        } else {
            console.log("Error fetching data:", response.statusText);
        }
    } catch (error) {
        console.error("Error fetching cases and lawyers:", error);
    }
}

// Function to update the lawyers table with the fetched data
function updateLawyersTable(data) {
    const tableBody = document.getElementById("lawyersTableBody");
    tableBody.innerHTML = ""; // Clear existing table rows

    if (!data || data.length === 0) {
        tableBody.innerHTML = "<tr><td colspan='9' class='text-center'>No data available</td></tr>";
        return;
    }

    data.forEach(lawyer => {
        const row = document.createElement("tr");

        row.innerHTML = `
            <td><input type="checkbox" class="caseCheckbox" data-case-id="${lawyer.caseID}" /></td>
            <th scope="row">${lawyer.lawyerID}</th>
            <td>${lawyer.caseID}</td>
            <td>${lawyer.lawyerName}</td>
            <td>${lawyer.lawyerFees}</td>
            <td>${lawyer.lawyerSpecialization}</td>
            <td>${lawyer.lawyerFees}</td>
            <td>${lawyer.lawyerRating}</td>
            <td>${lawyer.status}</td>
        `;

        tableBody.appendChild(row);
    });
}

// Function to get the selected case IDs from the checkboxes
function getSelectedCaseIDs() {
    const selectedCheckboxes = document.querySelectorAll(".caseCheckbox:checked");
    const selectedCaseIDs = [];

    selectedCheckboxes.forEach(checkbox => {
        const caseID = checkbox.getAttribute("data-case-id");
        selectedCaseIDs.push(parseInt(caseID, 10));
    });

    return selectedCaseIDs;
}

// Function to send DELETE request to the backend with selected case IDs
async function deleteCases(caseIDs) {
    const token = sessionStorage.getItem("authToken");

    if (!token) {
        console.log("No token found. Please log in.");
        return;
    }

    try {
        const response = await fetch("http://localhost:8081/user/Deletecases", {
            method: "DELETE",
            headers: {
                "Authorization": `Bearer ${token}`,
                "Content-Type": "application/json",
            },
            body: JSON.stringify(caseIDs),
        });

        if (response.ok) {
            const result = await response.json();
            console.log("Delete Response:", result);
            // Display success message and update the table
            alert("Cases deleted successfully!");
            removeDeletedRows(caseIDs);
            fetchCasesAndLawyers();
            location.reload();
        } else {
            const error = await response.json();
            console.log("Delete Error:", error);
            alert("Error deleting cases: " + error.message);
        }
    } catch (error) {
        console.error("Error deleting cases:", error);
        alert("An error occurred while deleting cases.");
    }
}

// Function to remove the deleted rows from the table
function removeDeletedRows(caseIDs) {
    const tableBody = document.getElementById("lawyersTableBody");
    const rows = tableBody.getElementsByTagName("tr");

    Array.from(rows).forEach(row => {
        const caseIDCell = row.getElementsByTagName("td")[2]; // Case ID is in the 3rd column
        if (caseIDCell) {
            const caseID = parseInt(caseIDCell.textContent, 10);
            if (caseIDs.includes(caseID)) {
                tableBody.removeChild(row); // Remove the row from the table
            }
        }
    });
}

document.addEventListener("DOMContentLoaded", function () {
    // Add event listener to the logout button
    const logoutButton = document.getElementById("logoutBtn");
    if (logoutButton) {
        logoutButton.addEventListener("click", function () {
            // Clear the session storage to remove the auth token
            sessionStorage.removeItem("authToken");
            
            // Optionally, clear other session-related data if needed
            // sessionStorage.clear(); // Uncomment to clear all session storage

            // Redirect to the home page
            window.location.href = "index.html";  // Change this URL to your home page URL
        });
    }
});

