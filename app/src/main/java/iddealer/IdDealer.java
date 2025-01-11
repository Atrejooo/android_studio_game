package iddealer;

public interface IdDealer {

    /**
     * defines a Class/type in this dealer and it's id if it is not already defined.
     */
    int defineType(Class type);

    int[] defineTypes(Class[] types);

    /**
     * returns the type id of a Class that was already defined.
     * This is how Idable instances get their typeIds
     * @param type as a Class object
     * @return the typeId
     * @throws IllegalArgumentException if the type was not yet defined
     */
    int typeToTypeId(Class type) throws IllegalArgumentException;
    /**
     * returns the instance that belongs to the specified instanceId.
     *
     * @param instanceId of the instance
     * @return the instance of that id
     */
    Idable fromInstanceId(int instanceId);

    /**
     * Retruns the class object that belongs to the typeId.
     *
     * @param typeId of the class
     * @return Class object of the typeId
     */
    Class fromTypeId(int typeId);

    /**
     * adds an instance to the list of managed Objects and sets the instanceId and typeId according
     * to it's class.
     *
     * @param idable to add
     */
    void addIdableInstance(Idable idable) throws IllegalArgumentException;

    /**
     * adds an instance to the list of managed Objects and sets the instanceId and typeId according
     * to it's class.
     *
     * @param idable to add
     */
    void addIdableInstance(Idable idable, int id) throws IllegalArgumentException;

    /**
     * removes an instance from the list of managed Objects. For example if the instance is no longer
     * needed.
     *
     * @param idable to remove
     */
    void removeIdableInstance(Idable idable);
}
