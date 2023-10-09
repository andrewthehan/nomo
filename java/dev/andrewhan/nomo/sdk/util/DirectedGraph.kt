package dev.andrewhan.nomo.sdk.util

class DirectedGraph<T>(
  private val edges: BiMultiMap<T, T> = HashBiMultiMap(),
  val nodes: MutableSet<T> = HashSet()
) {
  fun clone(): DirectedGraph<T> = DirectedGraph(edges.clone(), nodes.toMutableSet())

  fun contains(t: T): Boolean = nodes.contains(t)

  fun addNode(t: T): Boolean = nodes.add(t)

  fun addEdge(t1: T, t2: T) {
    if (!contains(t1) || !contains(t2)) {
      return
    }

    edges.put(t1, t2)
  }

  fun getOutgoingEdges(t: T): Set<T> = edges[t]

  fun getIncomingEdges(t: T): Set<T> = edges.getByValue(t)

  fun hasOutgoingEdges(t: T): Boolean = getOutgoingEdges(t).any()

  fun hasIncomingEdges(t: T): Boolean = getIncomingEdges(t).any()

  fun removeEdge(t1: T, t2: T): Boolean {
    return if (nodes.contains(t1) && nodes.contains(t2)) {
      edges.remove(t1, t2)
    } else {
      false
    }
  }

  fun removeNode(t: T): Boolean {
    val nodeExisted = nodes.remove(t)
    return if (nodeExisted) {
      edges.removeKey(t) // remove outgoing edges
      edges.removeValue(t) // remove incoming edges
      true
    } else {
      false
    }
  }
}

/** kahn's algorithm */
fun <T> DirectedGraph<T>.getTopologicalSort(): List<T> {
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
    throw IllegalStateException(
      "Unable to determine a topological sort due to cycles detected for: ${clone.nodes}"
    )
  }

  return sort
}
