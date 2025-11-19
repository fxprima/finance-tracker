function mapBackendTransactions(rawList) {
    return rawList.map(tx => ({
        date: tx.date,                     
        type: tx.transactionType,          
        category: tx.category || 'Uncategorized',
        subCategory: tx.subCategory || '',
        currency: tx.currency || 'IDR',
        amount: tx.amount || 0,
        note: tx.note || ''
    }));
}

document.addEventListener('DOMContentLoaded', function () {

    if (window.FINANCE_TRANSACTIONS && window.FINANCE_TRANSACTIONS.length > 0) {
        transactions = mapBackendTransactions(window.FINANCE_TRANSACTIONS);
    } 

    console.log(transactions);
});
