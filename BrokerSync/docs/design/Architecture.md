### Product and Contract Management Module
   
  Responsibilities:
    - Product and Contract Catalog: Manages a comprehensive catalog of all products (e.g., commodities, financial instruments), contracts (e.g., futures, options), delivery dates, and vintages available within the system.
    - Validation Services: Validates that all incoming orders reference valid products, contracts, delivery dates, and vintages. Ensures that the system is aware of all specific attributes related to each product.
    - API for Querying Details: Provides an API or service that other modules can use to query product and contract details to ensure consistency across the system.

  Subcomponents:
    - Manual Product Catalog Management: Provides backend processes for manually managing the product and contract catalog. This includes adding, updating, or removing products, contracts, delivery dates, and vintages. The component is designed to handle scenarios where automated data feeds may not capture all the necessary details or where human oversight is required to maintain data accuracy and completeness.
    - Product Catalog Service: Centralized service that manages the lifecycle of products, contracts, and associated attributes like delivery dates and vintages. *** NEEDS LOGIC.
    - Contract Validation Engine: Validates incoming orders against the catalog to ensure they reference valid products and contracts before they are processed further.
    - Attribute Manager: Manages specific attributes such as delivery dates and vintages, ensuring that they are consistently applied across all modules.

  Integration with Iceberg: ***MAY SWITCH TO PSQL
    - Product and Contract Table: An Iceberg table that stores all product, contract, delivery, and vintage details, ensuring the system can track changes over time and easily retrieve this information for processing and analysis.

### Order Intake Module

  Responsibilities:
    - Order Ingestion: Receives orders via the Order Events Topic from external systems, categorizing them by product, contract, delivery date, and vintage.
    - Order Validation: Validates each order against the catalog provided by the Product and Contract Management Module, ensuring it references valid product and contract details.
    - Order Processing: Publishes validated orders to the Order Book Updates Topic for inclusion in the respective order books and stores them in Iceberg for historical tracking.
  
  Subcomponents:
    - Order Validator: Consumes orders from the Order Events Topic, validates them against the catalog, and routes them to the appropriate order book via the Order Book Updates Topic.
    - Order Processor: Handles the categorization and processing of orders based on their product, contract, delivery date, and vintage, ensuring they are correctly managed throughout the system.
  
  Integration with Iceberg:
    - Order History Table: An Iceberg table that stores all orders along with their full details, allowing the system to maintain a comprehensive history for auditing and analytics.

### Order Book Module
   
  Responsibilities:
    - Order Book Management: Maintains a distinct order book for each product, contract, delivery date, and vintage, allowing for precise matching of orders.
    - Real-Time Updates: Updates the order books in real-time based on messages from the Order Book Updates Topic, ensuring that the latest orders are always included.
    - Snapshot and Versioning: Periodically stores snapshots of the order book in Iceberg, enabling time travel and historical analysis of order book states.
  
  Subcomponents:
    - Buy-Side and Sell-Side Order Books: Maintains separate order books for buy and sell orders, categorized by product and contract details.
    - Order Book Snapshot Manager: Regularly takes snapshots of the order book’s state and stores these snapshots in Iceberg for future reference.
  
  Integration with Iceberg:
    - Order Book Snapshot Table: An Iceberg table that stores snapshots of order books, categorized by product, contract, delivery date, and vintage, enabling historical analysis and backtesting.

### Matching Algorithm Module
   
  Responsibilities:
    - Order Matching: Matches buy and sell orders within the same product, contract, delivery date, and vintage, using various matching algorithms tailored to the specific needs of each product type.
    - Algorithm Flexibility: Supports different matching algorithms (e.g., price-time priority, pro-rata) depending on the characteristics of the product and contract.
    - Real-Time Processing: Consumes order book updates in real-time from the Order Book Updates Topic, processes matches, and publishes matched trades to the Matched Trades Topic.
  
  Subcomponents:
    - Matching Engine: The core component that implements the matching logic, ensuring that orders are matched efficiently and fairly based on the selected algorithm.
    - Partial Fill Manager: Manages orders that are partially filled, ensuring that remaining quantities are correctly updated and re-queued for further matching.
  
  Integration with Iceberg:
    - Matched Trades Table: An Iceberg table that stores all matched trades, categorized by product, contract, delivery date, and vintage, enabling detailed historical analysis and reporting.

 ### Price Discovery Module
   
  Responsibilities:
    - Price Calculation: Calculates the assesed/indicative price for each contract based on the current state of the order book and historical trades the contract and related or correlated contracts. 
    - Real-Time Updates: Provides real-time price updates to the system, publishing current assessed prices to the Product & Contract Management Module.
  
  Subcomponents:
    - Price Assessment Engine: Consumes data from the Order Book and Historical Trade Manager for the contract and related contracts and determines the assessed price for each contract.
  
  Integration with Platform: 

     - Inputs:
        1.) Historical Trade Data from Historical Trade Manager.
     - Exports:

      
### Trade Execution Module
  
  Responsibilities:
    - Trade Finalization: Consumes matched trades from the Matched Trades Topic and completes the trade process, including clearing and settlement.
    - Portfolio Updates: Updates client portfolios based on executed trades, ensuring that holdings reflect the latest matched and settled trades.
    - Real-Time Notifications: Sends real-time notifications to relevant parties (e.g., clients, brokers) about trade execution details.
  
  Subcomponents:
    - Trade Processor: Finalizes trades by processing the details of each matched trade, updating relevant systems, and ensuring that all financial and legal aspects of the trade are handled correctly.
    - Clearing and Settlement Engine: Manages the internal settlement process, ensuring that funds and assets are transferred accurately between parties.
  
  Integration with Iceberg:  
    - Trade Settlement Table: An Iceberg table that stores all finalized trades and settlement details, categorized by product, contract, delivery date, and vintage, providing a reliable record for auditing and reconciliation.
  
### Interaction Patterns and Data Flow
  
  Order Submission: Orders are submitted to the system via the Order Events Topic. The Order Intake Module validates and categorizes these orders based on product, contract, delivery date, and vintage.
  Order Book Management: Valid orders are published to the Order Book Updates Topic, where the Order Book Module updates the relevant order books. Periodic snapshots are stored in Iceberg.
  Order Matching: The Matching Algorithm Module consumes order book updates, matches orders within the same product and contract categories, and publishes matched trades to the Matched Trades Topic.
  Price Discovery: The Price Discovery Module calculates the best bid/ask prices and publishes these to the Matched Trades Topic and other relevant topics for real-time updates.
  Trade Execution: The Trade Execution Module consumes matched trades, finalizes the transactions, updates portfolios, and stores settlement details in Iceberg.
  Data Persistence and Analysis: All relevant data, including orders, matched trades, order book snapshots, and settlement details, are stored in Iceberg tables, categorized by product, contract, delivery date, and vintage, enabling robust historical analysis, compl
