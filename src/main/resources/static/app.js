document.addEventListener('DOMContentLoaded', () => {
    // State
    let currentUser = null;
    let isLoginMode = true;
    let currentAction = null; // 'deposit', 'withdraw', 'transfer'

    // DOM Elements
    const authView = document.getElementById('auth-view');
    const dashboardView = document.getElementById('dashboard-view');
    
    // Auth elements
    const tabLogin = document.getElementById('tab-login');
    const tabRegister = document.getElementById('tab-register');
    const authForm = document.getElementById('auth-form');
    const authSubmitBtn = document.getElementById('auth-submit');
    const authError = document.getElementById('auth-error');
    
    // Dashboard elements
    const userDisplay = document.getElementById('user-display');
    const balanceDisplay = document.getElementById('balance-display');
    const logoutBtn = document.getElementById('logout-btn');
    const historySection = document.getElementById('history-section');
    const transactionList = document.getElementById('transaction-list');
    
    // Action Modals
    const actionModal = document.getElementById('action-modal');
    const modalTitle = document.getElementById('modal-title');
    const actionForm = document.getElementById('action-form');
    const closeBtn = document.querySelector('.close-btn');
    const transferGroup = document.getElementById('transfer-group');
    const actionError = document.getElementById('action-error');
    const actionSuccess = document.getElementById('action-success');

    // Action Buttons
    const btnDeposit = document.getElementById('btn-deposit');
    const btnWithdraw = document.getElementById('btn-withdraw');
    const btnTransfer = document.getElementById('btn-transfer');
    const btnHistory = document.getElementById('btn-history');

    // Setup Tabs
    tabLogin.addEventListener('click', () => {
        isLoginMode = true;
        tabLogin.classList.add('active');
        tabRegister.classList.remove('active');
        authSubmitBtn.textContent = 'Login';
        authError.textContent = '';
    });

    tabRegister.addEventListener('click', () => {
        isLoginMode = false;
        tabRegister.classList.add('active');
        tabLogin.classList.remove('active');
        authSubmitBtn.textContent = 'Register';
        authError.textContent = '';
    });

    // Auth Submission
    authForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('username').value;
        const password = document.getElementById('password').value;
        
        const endpoint = isLoginMode ? '/api/auth/login' : '/api/auth/register';
        
        try {
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username, password })
            });
            const data = await res.json();
            
            if (res.ok) {
                currentUser = data;
                authForm.reset();
                showDashboard();
            } else {
                authError.textContent = data.error || 'Authentication failed';
            }
        } catch (err) {
            authError.textContent = 'Network error. Make sure backend is running.';
        }
    });

    // Logout
    logoutBtn.addEventListener('click', () => {
        currentUser = null;
        historySection.classList.add('hidden');
        authView.classList.add('active');
        dashboardView.classList.add('hidden');
        dashboardView.classList.remove('active');
        document.getElementById('username').value = '';
        document.getElementById('password').value = '';
    });

    function showDashboard() {
        authView.classList.remove('active');
        authView.classList.add('hidden');
        dashboardView.classList.remove('hidden');
        dashboardView.classList.add('active');
        updateDashboard();
    }

    function updateDashboard() {
        if (!currentUser) return;
        userDisplay.textContent = currentUser.username;
        balanceDisplay.textContent = currentUser.balance.toFixed(2);
    }

    // Modal Operations
    function openModal(action) {
        currentAction = action;
        actionModal.classList.remove('hidden');
        actionError.textContent = '';
        actionSuccess.textContent = '';
        actionForm.reset();
        
        if (action === 'deposit') {
            modalTitle.textContent = 'Deposit Funds';
            transferGroup.style.display = 'none';
            document.getElementById('target-username').removeAttribute('required');
        } else if (action === 'withdraw') {
            modalTitle.textContent = 'Withdraw Funds';
            transferGroup.style.display = 'none';
            document.getElementById('target-username').removeAttribute('required');
        } else if (action === 'transfer') {
            modalTitle.textContent = 'Transfer Funds';
            transferGroup.style.display = 'block';
            document.getElementById('target-username').setAttribute('required', 'true');
        }
    }

    btnDeposit.addEventListener('click', () => openModal('deposit'));
    btnWithdraw.addEventListener('click', () => openModal('withdraw'));
    btnTransfer.addEventListener('click', () => openModal('transfer'));
    
    closeBtn.addEventListener('click', () => {
        actionModal.classList.add('hidden');
    });

    window.addEventListener('click', (e) => {
        if (e.target === actionModal) {
            actionModal.classList.add('hidden');
        }
    });

    actionForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const amount = parseFloat(document.getElementById('amount').value);
        let endpoint = `/api/account/${currentUser.id}/${currentAction}`;
        let payload = { amount };

        if (currentAction === 'transfer') {
            const toUsername = document.getElementById('target-username').value;
            payload = { amount, toUsername };
        }

        try {
            const res = await fetch(endpoint, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            });
            const data = await res.json();
            
            if (res.ok) {
                actionSuccess.textContent = 'Transaction successful!';
                actionError.textContent = '';
                if (currentAction !== 'transfer') {
                    currentUser.balance = data.balance;
                } else {
                    currentUser.balance -= amount; // manually update since transfer doesn't return full account yet
                }
                updateDashboard();
                if (!historySection.classList.contains('hidden')) {
                    fetchHistory(); // refresh history if open
                }
                setTimeout(() => actionModal.classList.add('hidden'), 1500);
            } else {
                actionError.textContent = data.error || 'Transaction failed';
            }
        } catch (err) {
            actionError.textContent = 'Network error.';
        }
    });

    // History
    btnHistory.addEventListener('click', () => {
        historySection.classList.toggle('hidden');
        if (!historySection.classList.contains('hidden')) {
            fetchHistory();
        }
    });

    async function fetchHistory() {
        if (!currentUser) return;
        try {
            const res = await fetch(`/api/account/${currentUser.id}/transactions`);
            const data = await res.json();
            if (res.ok) {
                renderHistory(data);
            }
        } catch (err) {
            console.error('Error fetching history', err);
        }
    }

    function renderHistory(transactions) {
        transactionList.innerHTML = '';
        if (transactions.length === 0) {
            transactionList.innerHTML = '<li style="text-align:center; color: #94a3b8;">No transactions found.</li>';
            return;
        }
        
        transactions.forEach(tx => {
            const li = document.createElement('li');
            li.className = 'transaction-item fade-in';
            
            const date = new Date(tx.timestamp).toLocaleString();
            const isDeposit = tx.type.toLowerCase().includes('deposit') || tx.type.toLowerCase().includes('in');
            const sign = isDeposit ? '+' : '-';
            const colorClass = isDeposit ? 'positive' : 'negative';

            li.innerHTML = `
                <div class="transaction-info">
                    <span class="tx-type">${tx.type}</span>
                    <span class="tx-date">${date}</span>
                </div>
                <div class="tx-amount ${colorClass}">
                    ${sign}$${tx.amount.toFixed(2)}
                </div>
            `;
            transactionList.appendChild(li);
        });
    }
});
