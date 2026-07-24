/*
 * Copyright 2021 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.plusminus.hibernate;

import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import software.plusminus.scope.events.InvocationFinalizedEvent;
import software.plusminus.scope.events.InvocationStartedEvent;

import javax.persistence.EntityManager;

/**
 * Enables/disables Hibernate {@link org.hibernate.annotations.Filter}s for the duration of an
 * invocation.
 *
 * <p>Constraint: filters are enabled on the {@link Session} obtained from the shared
 * {@link EntityManager} proxy. For the filters to affect subsequent queries, the same session must
 * remain bound to the thread for the whole invocation. This holds under Spring's
 * open-session/EntityManager-in-view or when the invocation runs inside a single transaction. If no
 * session is bound to the thread when the invocation starts (no open-in-view and no active
 * transaction), the enabled filters apply to a transient session and later queries executed in
 * their own transactions will not be filtered.
 */
@SuppressWarnings("PMD.CloseResource")
@AllArgsConstructor
@ConditionalOnBean(HibernateFilter.class)
@Component
public class HibernateFilterListener {

    private EntityManager entityManager;
    private HibernateFilterService filterService;

    @EventListener
    public void onInvocationStarted(InvocationStartedEvent<?> event) {
        Session session = entityManager.unwrap(Session.class);
        filterService.enableFilters(session);
    }

    @EventListener
    public void onInvocationFinalized(InvocationFinalizedEvent<?> event) {
        Session session = entityManager.unwrap(Session.class);
        filterService.disableFilters(session);
    }
}
