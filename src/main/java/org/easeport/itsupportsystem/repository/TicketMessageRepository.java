package org.easeport.itsupportsystem.repository;

import org.easeport.itsupportsystem.model.mailRelated.TicketMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TicketMessageRepository extends JpaRepository<TicketMessage, Long> {

    public TicketMessage findByEmailMessageId(String inReplyTo);

}
