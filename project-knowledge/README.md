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

3. **Follow Established Patterns**: Use the patterns and guidelines documented in `architecture-patterns.md` and `development-guidelines.md`.

4. **Domain Understanding**: Review `tech-challenge.md` and `domain-model.md` to understand the business context and domain boundaries.

5. **Consistency is Key**: Maintain consistency with existing code, naming conventions, and architectural decisions.

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

### Essential Reading (in order of priority):
1. **Project Requirements** - Business needs, specifications, and deliverables
2. **Architecture Decision Records** - Architectural decisions and their rationale  
3. **Domain Documentation** - Domain understanding and bounded contexts
4. **Architecture Patterns** - Design patterns and implementation guidelines
5. **Development Guidelines** - Coding standards and conventions

### For Quick Reference:
- **Usage Instructions** - This README for how to use the knowledge base
- **Core Documents** - Most referenced architectural and domain information

## 🚀 Getting Started

When you (as an AI model or developer) start working on this project:

1. **Read the project requirements** to understand business needs and constraints
2. **Review architectural decisions** to understand established patterns and choices
3. **Study the domain model** to grasp bounded contexts and business rules
4. **Follow architecture patterns** for consistent implementation approaches
5. **Apply coding standards** to maintain code quality and consistency

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
