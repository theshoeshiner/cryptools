# Cryptools

A Crypto Portfolio Tracker / Balancer

# Cryptax

A capital gains calculator (Currently requires manual coding to import transaction files)

* Loads all past transactions and trades into memory
* Calculates cost basis for each specific share based on historical price using the cryptocompare.com API
* Maps all sells to a corresponding purchase, selecting the purchase that results in the least short/long term capital gain according to specified short/long term capital gains tax rate.
* Summarizes final report, providing the cumulative short/long term gain for the year.
