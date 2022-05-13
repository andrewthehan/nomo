package dev.andrewhan.nomo.sdk.collections

class Graph<T>(private val edges: BiMultiMap<T, T> = BiMultiMap(), private val nodes: MutableSet<T> = HashSet()) {
  private fun clone(): Graph<T> = Graph(edges.clone(), nodes.toMutableSet())

  fun contains(t: T) = nodes.contains(t)

  fun addNode(t: T) = nodes.add(t)

  fun addEdge(t1: T, t2: T) {
    if (!contains(t1) || !contains(t2)) {
      return
    }

    edges.put(t1, t2)
  }

  fun getOutgoingEdges(t: T) = edges[t]

  fun getIncomingEdges(t: T) = edges.getByValue(t)

  fun hasOutgoingEdges(t: T) = getOutgoingEdges(t).any()

  fun hasIncomingEdges(t: T) = getIncomingEdges(t).any()

  fun removeEdge(t1: T, t2: T) = nodes.contains(t1) && nodes.contains(t2) && edges.remove(t1, t2)

  fun removeNode(t: T) = nodes.remove(t) && edges.removeKey(t) != null && edges.removeValue(t) != null

  /**
   * kahn's algorithm
   */
  fun getTopologicalSort(): List<T> {
    val clone = clone()
    val sort = mutableListOf<T>()
    val noIncoming = clone.nodes.filter { !clone.hasIncomingEdges(it) }.toMutableSet()
    while (noIncoming.any()) {
      val node = noIncoming.first()
      noIncoming.remove(node)
      sort.add(node)
      val outgoing = clone.getOutgoingEdges(node)
      clone.removeNode(node)
      noIncoming.addAll(outgoing.filter { !clone.hasIncomingEdges(it) })
    }

    if (clone.nodes.any()) {
      throw IllegalStateException("Unable to determine a topological sort due to cycles detected for: ${clone.nodes}")
    }

    return sort
  }
}