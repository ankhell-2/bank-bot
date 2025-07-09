package github.ankhell.bank_bot.jpa.repositories

import github.ankhell.bank_bot.jpa.entities.SpiceRun
import org.springframework.data.jpa.repository.JpaRepository

interface SpiceRunRepository: JpaRepository<SpiceRun, Long>