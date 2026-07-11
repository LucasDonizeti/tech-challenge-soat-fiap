# Project Knowledge Base

## 📋 Purpose

This directory serves as a **centralized knowledge base** for the Tech Challenge SOAT FIAP project. It contains architectural decisions, design patterns, project requirements, and guidelines that should be used as reference for AI models, developers, and team members working on this project.

## 🎯 Target Audience

This knowledge base is designed to be consumed by:
- **AI Models** (including future contexts and different AI assistants)
- **Development Team Members**
- **Architects and Technical Leads**
- **New Project Contributors**

## 📁 Knowledge Base Evolution

This directory grows organically as the project evolves. Documents are added as needed to capture:
- Architectural decisions
- Domain insights  
- Development patterns
- Project requirements
- Team guidelines

## 🔍 How to Use This Knowledge Base

### For AI Models and Assistants

When working with this project, follow these guidelines:

1. **Always Start Here**: Before making any architectural or implementation decisions, review the relevant documents in this knowledge base.

2. **Reference ADRs First**: Check `adr.md` for any existing architectural decisions that might impact your work. Do not contradict established decisions without explicit justification.

3. **Domain & Architecture Understanding**: Review the architecture specifications in `architecture.md` and requirements in `tech-challenge.md` and `tech-challange-parte-2.md`.

4. **Consistency is Key**: Maintain consistency with existing code, naming conventions, and architectural decisions.

### Key Principles for AI-Assisted Development

- **Respect Existing Decisions**: All architectural decisions documented in ADRs should be considered binding unless explicitly revised
- **Domain-First Approach**: Always consider the domain model and business rules before technical implementation
- **Clean Architecture Compliance**: Follow the established layer structure and dependency rules
- **Test-Driven Development**: Ensure all new code includes appropriate tests following the project's testing strategy

## 🏗️ Architectural Context

This project implements:
- **Clean Architecture** with Domain-Driven Design
- **Multiple Bounded Contexts**: OrdemServico, Cliente, Estoque, Administrativo
- **Layered Structure**: Domain, Application, Infrastructure, Shared Kernel
- **DDD Patterns**: Aggregates, Entities, Value Objects, Domain Events

## 📚 Important Documents

### Essential Reading:
1. **Requirements & Context:**
   - [Fase 1 Requirements](tech-challenge.md) - Business needs, specifications, and deliverables of Phase 1.
   - [Fase 2 Requirements](tech-challange-parte-2.md) - Requirements and objectives of Phase 2 (In Progress).
2. **Architecture & Decisions:**
   - [Project Architecture (Clean Arch)](architecture.md) - Project package structure, layers, and context mapping.
   - [Architecture Decision Records (ADRs)](adr.md) - Record of architectural decisions made.

### Technical & DevOps Guides:
- [CI/CD Deployment Flow](ci-cd.md) - Details of GitHub Actions pipeline, AWS EKS, and Terraform setup.
- [Local Setup & Testing Guide](local-setup-testing.md) - Guide for running the app without Docker, testing with JaCoCo, SonarQube, and OWASP.
- [API Security & Authentication](api-authentication.md) - JWT configurations, login endpoints, public and private API details.

## 🚀 Getting Started

When you (as an AI model or developer) start working on this project:

1. **Read the project requirements** (`tech-challenge.md` and `tech-challange-parte-2.md`) to understand business needs and constraints.
2. **Review architectural decisions** (`adr.md`) and specifications (`architecture.md`) to understand established patterns and choices.
3. **Follow the CI/CD and Local Setup guides** (`ci-cd.md` and `local-setup-testing.md`) to prepare your development and deployment environment.

## ⚠️ Important Notes

- **Never bypass established architectural decisions** without documented justification
- **Always maintain consistency** with existing code and patterns
- **Consider the domain first** - technical solutions should serve business needs
- **Document new decisions** - if you need to make new architectural decisions, add them to `adr.md`

## 🔄 Maintaining This Knowledge Base

This knowledge base should be:
- **Updated** when new architectural decisions are made
- **Referenced** in pull requests and code reviews
- **Used** as the single source of truth for project architecture
- **Evolved** as the project grows and requirements change

---

## 🤖 AI Model Instructions

If you are an AI model assisting with this project:

1. **Treat this knowledge base as authoritative** - it represents the project's established architecture and guidelines
2. **Cross-reference documents** - don't rely on a single document, use multiple sources for complete understanding
3. **Ask for clarification** if requirements seem to conflict with established architectural decisions
4. **Suggest improvements** through proper channels (ADR updates, documentation enhancements)
5. **Maintain context** - remember the architectural patterns and domain boundaries throughout your interactions

**Remember**: This knowledge base exists to ensure consistency, quality, and proper architectural alignment across all project work, whether performed by humans or AI assistants.
