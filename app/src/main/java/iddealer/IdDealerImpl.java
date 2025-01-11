package iddealer;

import java.util.HashMap;
import java.util.Map;

class IdDealerImpl implements IdDealer {

    // Maps to store instances by their instanceId and types by their typeId
    private Map<Integer, Idable> instanceMap;
    private Map<Integer, Class> typeMap;
    private Map<Class, Integer> typeToIdMap;
    private int currentInstanceId;
    private int currentTypeId;

    // Constructor to initialize maps
    public IdDealerImpl() {
        this.instanceMap = new HashMap<>();
        this.typeMap = new HashMap<>();
        this.typeToIdMap = new HashMap<>();
        this.currentInstanceId = 1;  // Start instance ID from 1
        this.currentTypeId = 1;  // Start type ID from 1
    }

    @Override
    public int defineType(Class type) {
        // Check if the type already exists in the type map
        if (!typeToIdMap.containsKey(type)) {
            // Define a new typeId for this class
            int newTypeId = currentTypeId++;
            typeToIdMap.put(type, newTypeId);
            typeMap.put(newTypeId, type);
            return newTypeId;
        }
        // Return existing typeId if the class is already defined
        return typeToIdMap.get(type);
    }

    @Override
    public int[] defineTypes(Class[] types) {
        int[] resultIds = new int[types.length];

        for (int i = 0; i < types.length; i++) {
            resultIds[i] = defineType(types[i]);
        }

        return resultIds;
    }

    @Override
    public int typeToTypeId(Class type) throws IllegalArgumentException {
        if (typeToIdMap.containsKey(type)) {
            return typeToIdMap.get(type);
        } else {
            throw new IllegalArgumentException("the type was never defined: " + type);
        }
    }


    @Override
    public Idable fromInstanceId(int instanceId) {
        return instanceMap.get(instanceId);
    }


    @Override
    public Class fromTypeId(int typeId) {
        return typeMap.get(typeId);
    }


    @Override
    public void addIdableInstance(Idable idable) throws IllegalArgumentException {
        Class type = idable.getClass();

        // Ensure that the type is valid
        if (typeToIdMap.containsKey(type)) {
            int typeId = typeToIdMap.get(type);

            // Set the instance and type Id
            idable.setInstanceId(currentInstanceId);
            idable.setTypeId(typeId);

            // Add to instance map
            instanceMap.put(currentInstanceId, idable);
            currentInstanceId++;
        } else {
            throw new IllegalArgumentException("Type not defined for the provided instance");
        }
    }

    @Override
    public void addIdableInstance(Idable idable, int id) throws IllegalArgumentException {
        Class type = idable.getClass();
        if (typeToIdMap.containsKey(type)) {
            int typeId = typeToIdMap.get(type);

            // Set the instance and type Id
            idable.setInstanceId(id);
            idable.setTypeId(typeId);

            // Add to instance map
            instanceMap.put(id, idable);
            currentInstanceId = id + 1;
        } else {
            throw new IllegalArgumentException("Type not defined for the provided instance");
        }
    }


    @Override
    public void removeIdableInstance(Idable idable) {
        // Remove instance by its instanceId
        instanceMap.remove(idable.instanceId());
    }
}
