internal class a {
    val lastDamageCause: Unit

    init {
        val nEvent: EntityDamageByEntityEvent = event
            .getEntity().getLastDamageCause() as EntityDamageByEntityEvent
    }
}