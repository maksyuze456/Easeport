# Easeport
**EasePort** is a AI-Powered Email-to-Ticket System which acts as a self-hosted support automation platform that converts incoming emails into categorized support tickets.  
The system connects to existing IMAP mailboxes, processes messages through AI classification, and exposes them through a secure RESTful API and a modern web dashboard.


# Features
- **Spring Boot Backend**  
  Retrieves and processes incoming emails via IMAP, integrates with AI for categorization, and exposes REST APIs for ticket management.  
  Includes Spring Security with JWT-based authentication and role-based access control.

- **AI-Driven Classification**  
  Uses an AI model to analyze and automatically categorize emails (e.g., *IT Support*, *General Inquiry*, *Technical Issue*).

- **MySQL Database**  
  Stores user data, categorized tickets, and answers for persistence and analytics.

- **Next.js + React Frontend**  
  Provides a responsive interface for viewing, updating, and closing tickets with built-in authentication and session handling.

- **Continuous Integration Pipeline**  
  Automated build and test flow ensuring reliability and deployment consistency.

- **Docker Support**  
  Includes Dockerfiles and a `docker-compose.yml` for containerized local deployment.

# Prerequisites

Before running the project locally, ensure the following are installed:

- Java 21+  
- Node.js and npm    
- Docker *(MySQL, Maven and Java 21 Development Kit for container setup)*  
- IDE *(IntelliJ IDEA, VS Code, etc.)*

