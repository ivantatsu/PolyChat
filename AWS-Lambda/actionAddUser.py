import boto3
import json

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')

def update_user(user_id, related_user_id, add_to_contacts):
    # Obtén el item del usuario
    response = table.get_item(
        Key={
            'user_id': user_id
        }
    )
    if 'Item' in response:
        user_item = response['Item']

        # Si 'related_user_id' está en 'requests', lo elimina
        if 'requests' in user_item and related_user_id in user_item['requests']:
            user_item['requests'].remove(related_user_id)

            # Si 'requests' está vacío, lo elimina del objeto
            if not user_item['requests']:
                user_item.pop('requests')

        # Si 'add_to_contacts' es verdadero, añade 'related_user_id' a 'contactos'
        if add_to_contacts:
            if 'contactos' in user_item:
                user_item['contactos'].add(related_user_id)
            else:
                user_item['contactos'] = {related_user_id}

        # Actualiza el item del usuario en la base de datos
        table.put_item(Item=user_item)

        return {
            'statusCode': 200,
            'body': json.dumps({'message': 'User updated successfully'})
        }
    else:
        return {
            'statusCode': 404,
            'body': json.dumps({'message': 'User not found'})
        }

def lambda_handler(event, context):
    # Obtén los datos del evento
    user_id = event['user_id']
    related_user_id = event['related_user_id']
    add_to_contacts = event.get('add_to_contacts', False)

    # Actualiza el usuario
    response = update_user(user_id, related_user_id, add_to_contacts)

    return response
