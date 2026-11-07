![專案相關圖片](frontend/src/assets/logo3.png)

# Empowering Your Business to Soar 🚀

"Make your entrepreneurial journey smooth and stress-free\!"
---

🌐 **Multilingual Support (README)**
To serve a wider audience, this project's README document supports multiple languages. You can click the links below to switch languages:
Read in other languages: [English](README_en.md) | [中文](README.md)
---
## Table of Contents

* [✨ Project Core Philosophy](https://www.google.com/search?q=%23project-core-philosophy)
* [🌟 Feature Highlights at a Glance](https://www.google.com/search?q=%23feature-highlights-at-a-glance)
    * [Customer Relationship Management (CRM) 📈](https://www.google.com/search?q=%23customer-relationship-management-crm-)
    * [Enterprise Resource Planning (ERP) 📦](https://www.google.com/search?q=%23enterprise-resource-planning-erp-)
    * [E-commerce Frontend (B2C) 🛒](https://www.google.com/search?q=%23e-commerce-frontend-b2c-)
    * [Content Management System (CMS) ✍️](https://www.google.com/search?q=%23content-management-system-cms-)
* [⚙️ Deployment & Quick Start Guide](https://www.google.com/search?q=%23deployment-and-quick-start-guide)
    * [1. Live Demo](https://www.google.com/search?q=%231-live-demo)
    * [2. Get Project Code](https://www.google.com/search?q=%232-get-project-code)
    * [3. Backend Setup](https://www.google.com/search?q=%233-backend-setup)
    * [4. Frontend Setup](https://www.google.com/search?q=%234-frontend-setup)
* [📚 API Documentation (Swagger)](https://www.google.com/search?q=%23api-documentation-swagger)
* [💡 Future Outlook (AI Integration - Beginner Concepts)](https://www.google.com/search?q=%23future-outlook-ai-integration---beginner-concepts)
* [🤝 Contribution & Collaboration](https://www.google.com/search?q=%23contribution-and-collaboration)
    * [Development Guidelines](https://www.google.com/search?q=%23development-guidelines)
    * [Team Members](https://www.google.com/search?q=%23team-members)

## ✨ Project Core Philosophy

In the rapidly changing entrepreneurial landscape, small and medium-sized business owners often struggle due to a lack of integrated sales and customer management tools. **Haagen-Lyons** emerges to address this need, aiming to create an affordable, powerful, and intuitive backend system that allows you to focus on your products and customers, rather than tedious administrative tasks.

We firmly believe that data should not just be numbers, but a wise compass to guide your decisions. **Haagen-Lyons** transforms complex data into clear insights, enabling you to easily grasp your operational pulse, improve efficiency, revitalize customer relationships, and achieve sweeter entrepreneurial success\!

## 🌟 Feature Highlights at a Glance

### Customer Relationship Management (CRM) 📈

* **Unified Customer View:** Centralized management of customer profiles, interaction records, and tags, enabling personalized interactions and precise marketing.
* **Sales Funnel Optimization:** Track opportunities through every stage from lead to conversion, visualizing sales progress to help your team manage sales opportunities effectively.
* **Automated Reminders:** Never miss important follow-ups or key milestones (e.g., payment due dates, delivery dates, long-inactive customers), ensuring excellent customer relationship maintenance.
* **Performance Analytics:** Generate reports on customer sources, marketing campaign effectiveness, and detailed sales data to support data-driven decision-making.

### Enterprise Resource Planning (ERP) 📦

* **Smart Inventory Management:** Real-time inventory levels, automated reorder alerts, raw material flow tracking, and inventory turnover analysis, effectively reducing inventory costs and risks.
* **Optimized Order Processing:** Comprehensive management of sales orders (B2C & B2B) and purchase orders, with support for barcode-based outbound inspection, ensuring smooth and accurate logistics.
* **Flexible Pricing Rules:** Supports customer-specific pricing and unit pricing policies to meet diverse business needs.
* **Financial Insights:** Provides basic sales and profit reports, with support for Excel export and printing, helping you quickly grasp your financial status.

### E-commerce Frontend (B2C) 🛒

* **Engaging Product Display:** Highlight your brand story and product appeal through dynamic carousels, detailed product pages, and real-time inventory updates for limited edition items, attracting consumers.
* **Seamless Shopping Experience:** Intuitive shopping cart and checkout processes, supporting various payment and logistics options (e.g., refrigerated delivery, convenience store pickup), enhancing customer satisfaction.
* **Personalized Member Center:** Customers can manage their personal information, view order history, and utilize shopping credits and coupons, increasing customer loyalty.

### Content Management System (CMS) ✍️

* **Website Content Management:** Easily update website content, promotional activities, and latest news, keeping your website dynamic and current.
* **Marketing Performance Analysis:** Analyze campaign and advertising performance, with backend support for statistical calculations and data cleansing, optimizing your marketing strategies.
* **Customer Service Center:** Efficiently handle customer inquiries, feedback and complaints, improving customer service quality. Future integration with chatbots can provide 24/7 instant responses.

## ⚙️ Deployment & Quick Start Guide

This project adopts a decoupled frontend and backend architecture, with React for the frontend and Spring Boot for the backend.

### 1\. Live Demo

There is no live environment at the moment. Follow the steps below to run the project locally.

### 2\. Get Project Code

```bash
git clone https://github.com/Bennygoat/CRM.git
cd CRM
```

### 3\. Backend Setup

* **Navigate to the `backend/backend` directory.**

* **Configure Database:**
  Open `src/main/resources/application.properties` and modify the following settings according to your MySQL environment:

  ```properties
  spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
  spring.datasource.username=your_username
  spring.datasource.password=your_password
  ```

  Replace `your_database_name`, `your_username`, and `your_password` with your actual settings.

  **Important Note:** Before running the backend application, please ensure your MySQL database has been created and `your_database_name` exists. You can use tools like MySQL Workbench or DBeaver to manage your database.




* **Run Backend Application:**
  ```bash
  # In the backend/backend directory
  ./mvnw spring-boot:run
  ```

  The backend service will run by default on `http://localhost:8080`.

### 4\. Frontend Setup

* **Navigate to the `frontend` directory.**

* **Install Dependencies and Start Development Server:**

  ```bash
  npm install # Install dependencies
  npm run dev # Start development server
  ```

  The frontend application will run by default on `http://localhost:5173` (or as shown in your terminal).

## 📚 API Documentation (Swagger)

Our API documentation is automatically generated via Swagger, providing clear interface descriptions and testing capabilities.

🔗 [**Online API Documentation (Stoplight)**](https://joshkuei.stoplight.io/docs/eeit04-crm/6msrw1ug1vxy-cms-erp-crm-api)

Through this interface, you can understand detailed information about each API endpoint, request parameters, response formats, and perform direct testing.

## 💡 Future Outlook (AI Integration - Beginner Concepts)

We are excited about integrating AI technologies into the system. In the initial phase, our envisioned application scenarios include:

* **Smart Recommendations:** Based on customer purchase history and Browse behavior, automatically recommend relevant products or promotional activities to enhance the shopping experience and sales. This can start with simple**Content-based** recommendations, such as suggesting products similar to those already purchased by the customer.
* **Data Trend Analysis:** Utilize basic data aggregation and statistical methods to provide deeper market trend reports, such as analyzing the ROI of different advertising channels and identifying seasonal best-sellers. This will help operators make more informed business decisions.
* **Anomaly Detection:** Apply simple thresholding or statistical models to automatically detect unusual orders, abnormal inventory fluctuations, and issue timely alerts to prevent potential risks. For example, setting thresholds for daily order counts or individual order amounts.

These functionalities will be introduced incrementally in a modular fashion to ensure system stability and maintainability.

## 🤝 Contribution & Collaboration

We welcome contributions in any form\! If you have any suggestions, feature requests, or find bugs, please feel free to open an Issue or submit a Pull Request.

### Development Guidelines

* **TDD (Test-Driven Development):** We encourage writing unit tests before developing new features to ensure code quality and functional stability. This helps in identifying and fixing issues early in the development cycle.
* **Code Comments:** Please add clear and concise comments in critical logic sections to improve code readability and maintainability.
* **Iterative Development:** Follow Agile development principles, progressing the project in small, rapid steps, delivering working features in each iteration.

### Team Members

* Yang Zi-Min (Team Lead)
* Hou You-Lin
* Lin Ju-Chen
* Kuei Wei-Pang
* Guo Zhan-Shuo
* Chen Rui-Yu

<!-- end list -->

```
```