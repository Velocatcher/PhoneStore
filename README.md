ТЗ (Техническое задание)
1) Цель

Онлайн-магазин смартфонов с каталогом, корзиной, оформлением заказа, оплатой, доставкой, отзывами и админкой. Масштабируемость под сезонные пики, отказоустойчивость, быстрый поиск и безопасная оплата. Да, звучит как «хочу всё и сразу», но делаем по уму и итерациями.

2) Пользовательские сценарии (MVP → расширения)

Гость: просмотр каталога, фильтры/поиск, характеристики, наличие, цена, акции.

Юзер: регистрация/вход, корзина, оформление заказа, выбор доставки/оплаты, трекинг заказа, отзывы, гарантия/возврат.

Админ/контент: управление товарами, ценами, остатками, промо, баннерами, модерация отзывов, отчеты.

3) Микросервисы (границы и кратко про данные)

api-gateway: входная точка, rate limiting, CORS, маршрутизация, circuit breaker.

auth: пользователи, роли, JWT/OAuth2, SSO (Keycloak).

catalog: товары, бренды, спецификации, изображения (MinIO), категории. Read-heavy.

pricing: базовые цены, акции, купоны, динамическая цена, налоги.

inventory: склады, остатки, резервы, списания, события «stock-changed».

search: полнотекстовый поиск/фасеты (OpenSearch/Elasticsearch), индексирование из catalog.

cart: корзина, промокоды, пересчет цены и доступности в реальном времени.

checkout: оформление заказа, валидация адреса/доставки/оплаты.

order: жизненный цикл заказа, статусы, трекинг, документы. Сага «оплата→резерв→подтверждение».

payment: интеграции PSP (карты, Apple/Google Pay), 3DS, вебхуки.

shipping: тарифы и СДЭК/Boxberry/PostNL и т.п., трекинг, ярлыки.

notification: email/SMS/push/WhatsApp, шаблоны, лог исходящих.

review: отзывы/рейтинг, модерация, антиспам.

promo: купоны, персональные предложения, правила.

analytics: события, витрины для отчетов, дашборды.

backoffice: единая админ-UI, дергает внутренние API.

audit: трассировка действий, неизменяемый журнал.

return-warranty: RMA, возвраты, гарантийные обращения.

Минимальный MVP можно запустить с 1–10, а остальное довинчивать.

4) Технологический стек

Язык/Runtime: Java 21

Фреймворк: Spring Boot 3.x, Spring Cloud 2024.x, Spring Security, Spring Data

API: REST + OpenAPI 3, внутренние каналы — gRPC где нужно низкая латентность

База: PostgreSQL (OLTP), Redis (кэш/сессии/блокировки), OpenSearch (поиск), MinIO (медиа)

Сообщения: Kafka (EDA, outbox, ретраи, DLQ)

Идентификация: Keycloak (OAuth2/OIDC), JWT, mTLS внутри кластера опционально

Инфра: Docker, Kubernetes, Helm, Kustomize

CI/CD: GitHub Actions, ArgoCD

Обсервабилити: OpenTelemetry, Jaeger/Tempo, Prometheus + Grafana, ELK/OpenSearch Dashboards

Тесты: JUnit 5, Testcontainers, WireMock, Pact (CDC), Gatling/JMH для перфа

Frontend (вкратце): SPA (React/Next) + BFF на gateway или отдельный bff-service

5) НФТ (некомф. требования)

Доступность: 99.9% для публичных путей

Производительность: P95 каталог < 200 мс; чекаут P95 < 500 мс

Масштабирование: горизонтальное, авто-скейл по CPU/QPS/lag Kafka

Безопасность: OWASP ASVS L2, PCI-friendly изоляция payment, шифрование PII, секреты в Vault/Secrets

Стабильность: идемпотентные команды, retry с backoff, circuit breaker, rate limit

Данные: миграции Liquibase, резервные копии, GDPR (удаление/анонимизация)

6) Архитектурные паттерны

EDA через Kafka, SAGA оркестрация заказов, Outbox + Debezium/интерн. паблишер, CQRS для каталог/поиск

Кэширование read-моделей, TTL/инвалидизация по событию

Idempotency-Key для POST/оплата/чекаут

Политики консистентности: каталог eventually consistent с поиском; заказ — оркестрируемая сагой

7) Модели и события (вкратце)

Product, Sku, Price, StockItem, Cart, Order, Payment, Shipment, Review

Kafka топики: product-updated, price-updated, stock-changed, order-created, order-paid, order-cancelled, payment-authorized/failed, shipment-created, review-submitted

8) API контракты (намёки)

GET /api/catalog/products?query=&filters=&sort=&page=

POST /api/cart/{cartId}/items

POST /api/checkout

POST /api/payment/{orderId}/authorize

GET /api/order/{orderId}
Все контракты фиксируются в OpenAPI, генерим клиенты, валидируем Pact-тестами.

9) Безопасность

OAuth2 Authorization Code + PKCE для SPA

RBAC: USER, ADMIN, CONTENT, SUPPORT

Политики rate limiting на gateway

Аудит входных событий и админ-действий

План реализации по этапам
Этап 0. Базовая инфраструктура и скелет

Монорепо с многомодульным Maven или полирепо (рекомендую полирепо + общая BOM и starter-модули).

Общая платформа: platform-bom, common-starters (observability, security, tracing, error model).

Dev окружение: Docker Compose для Postgres/Redis/Kafka/MinIO/OpenSearch/Keycloak.

CI: линт/сборка, unit + Testcontainers, публикация образов, Helm-чарты, sandbox-развёртывание.

DoD: все сервисы «hello, health/liveness/readiness», централизованные логи/метрики/трейсы, one-click локальный ап.

Этап 1. Каталог + Поиск (read-путь)

catalog CRUD, версии схемы через Liquibase.

Индексатор → search, синхронизация событиями product-updated.

api-gateway публичные маршруты для browse/list/detail.

Кэширование витрин (Redis), фасеты/фильтры.

DoD: P95 < 200 мс, пагинация/сортировки, первые дашборды.

Этап 2. Корзина и ценообразование

cart хранит позиции, пересчет через pricing (синхронно) + промокоды из promo.

Идемпотентность операций, lock по cartId в Redis.

DoD: стабильные расчеты, негативные кейсы, контракт-тесты.

Этап 3. Оформление, заказы, остатки

checkout агрегирует валидации, создает заказ в order.

Сага: payment.authorize → inventory.reserve → order.confirm. Компенсации при сбоях.

inventory резервы и списания, события stock-changed.

DoD: устойчивый happy-path + полный набор компенсирующих сценариев.

Этап 4. Оплаты и доставка

payment интеграции PSP, 3DS, вебхуки, токенизация; PCI-зона изолирована.

shipping тарифы, выбор службы, трекинг статусов.

notification письма/SMS по событиям заказа и оплаты.

DoD: авторизация/капча/фрод-сигналы, ручные ретраи в админке.

Этап 5. Админка и контент

backoffice SPA для админов (каталог, цены, остатки, промо, баннеры).

review с модерацией, антиспам-правила.

DoD: разграничение прав, аудит действий.

Этап 6. Аналитика, промо, устойчивость

analytics витрины, RFM/ABC отчеты, промо-правила в promo.

Хаос-тесты, fault-injection, перф-прогоны (каталог/чекаут).

DoD: автоскейл и SLA подтверждены нагрузочными тестами.

Репозитории и сборка

platform-bom — версии зависимостей

common-libs — DTO, error model, tracing, security utils

сервисы: по одному репо, единые стандарты, общие GitHub Actions шаблоны

Helm чарты: /deploy/helm/<service> + общий umbrella chart

Данные и миграции

Каждому сервису свой Postgres, строгая изоляция схем

Liquibase в каждом сервисе

Outbox таблицы там, где есть события, воркер публикует в Kafka

Политика бэкапов и PITR; миграции поднимаются на стейдже перед продом

Тестовая стратегия

Unit + Mutation testing критичных модулей

Testcontainers для интеграции

WireMock/MockServer для внешних интеграций

Pact для потребитель-провайдер контрактов

E2E happy-path в nightly

Gatling перф, JMH точечные бенчмарки

Безопасность и комплаенс

Секреты только в K8s Secrets/External Secrets + KMS

PII шифруем на уровне БД или поля

Логи без персональных и карточных данных

Политики паролей, MFA для админов, журнал аудита