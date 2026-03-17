// Utility functions for the Cricket Auction Application

// API Base URL
const API_BASE_URL = '/api';

// Helper function to make API calls
async function apiCall(endpoint, method = 'GET', body = null) {
    const options = {
        method: method,
        headers: {
            'Content-Type': 'application/json'
        }
    };

    if (body) {
        options.body = JSON.stringify(body);
    }

    try {
        const response = await fetch(`${API_BASE_URL}${endpoint}`, options);

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('Resource not found');
            }
            const error = await response.json();
            throw new Error(error.message || 'API Error');
        }

        if (response.status === 204) {
            return null;
        }

        return await response.json();
    } catch (error) {
        console.error('API Error:', error);
        throw error;
    }
}

// Format currency
function formatCurrency(amount) {
    return '₹' + parseFloat(amount).toFixed(2);
}

// Format date
function formatDate(dateString) {
    const options = { year: 'numeric', month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' };
    return new Date(dateString).toLocaleDateString('en-IN', options);
}

// Get status badge color
function getStatusColor(status) {
    switch(status) {
        case 'REGISTERED':
            return '#bee3f8';
        case 'SOLD':
            return '#c6f6d5';
        case 'UNSOLD':
            return '#fed7d7';
        default:
            return '#e2e8f0';
    }
}

// Validation functions
function validateTeamForm(teamData) {
    const errors = [];

    if (!teamData.teamName || teamData.teamName.trim() === '') {
        errors.push('Team name is required');
    }

    if (!teamData.captain || teamData.captain.trim() === '') {
        errors.push('Captain name is required');
    }

    if (!teamData.totalBudget || teamData.totalBudget <= 0) {
        errors.push('Total budget must be greater than 0');
    }

    if (teamData.playerRetention1 && !teamData.playerRetention1Money) {
        errors.push('Retention 1 amount is required if retention 1 player is specified');
    }

    if (teamData.playerRetention2 && !teamData.playerRetention2Money) {
        errors.push('Retention 2 amount is required if retention 2 player is specified');
    }

    return errors;
}

function validatePlayerForm(playerData) {
    const errors = [];

    if (!playerData.playerName || playerData.playerName.trim() === '') {
        errors.push('Player name is required');
    }

    if (!playerData.age || playerData.age < 18 || playerData.age > 50) {
        errors.push('Age must be between 18 and 50');
    }

    if (!playerData.role) {
        errors.push('Role is required');
    }

    if (!playerData.basePrice || playerData.basePrice <= 0) {
        errors.push('Base price must be greater than 0');
    }

    return errors;
}

// Show notification
function showNotification(message, type = 'info', duration = 3000) {
    const notification = document.createElement('div');
    notification.className = `notification notification-${type}`;
    notification.textContent = message;
    notification.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 1rem 1.5rem;
        background-color: ${type === 'success' ? '#48bb78' : type === 'error' ? '#f56565' : '#3182ce'};
        color: white;
        border-radius: 4px;
        z-index: 9999;
        box-shadow: 0 2px 10px rgba(0,0,0,0.2);
    `;

    document.body.appendChild(notification);

    setTimeout(() => {
        notification.remove();
    }, duration);
}

// Format player role for display
function formatRole(role) {
    return role.replace(/-/g, ' ');
}

// Calculate team spent amount
function calculateTeamSpent(team) {
    if (!team.players || team.players.length === 0) {
        return 0;
    }
    return team.players.reduce((total, player) => {
        return total + (player.soldPrice || 0);
    }, 0);
}

// Export functions for use in HTML
window.formatCurrency = formatCurrency;
window.formatDate = formatDate;
window.validateTeamForm = validateTeamForm;
window.validatePlayerForm = validatePlayerForm;
window.showNotification = showNotification;
window.apiCall = apiCall;

