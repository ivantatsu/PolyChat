
import boto3
import json
import simplejson as json

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')

def get_related_users(user_id):
    response = table.get_item(
        Key={
            'user_id': user_id
        }
    )

    if 'Item' in response:
        user_item = response['Item']
        request_column = user_item.get('contactos', [])

        related_users = []
        for related_user_id in request_column:
            related_response = table.get_item(
                Key={
                    'user_id': related_user_id
                }
            )
            if 'Item' in related_response:
                related_user_item = related_response['Item']
                # Sólo se incluyen los campos requeridos en el diccionario
                user_data = {
                    'user_id': related_user_item.get('user_id'),
                    'userName': related_user_item.get('userName'),
                    'status': related_user_item.get('status')
                }
                related_users.append(user_data)

        return related_users
    else:
        return []



def lambda_handler(event, context):
    # Obtén el ID de usuario del evento
    body = json.loads(event["application/json"])
    user_id = body['user_id']

    # Obtén los usuarios relacionados en la columna "request" para el usuario proporcionado
    users = get_related_users(user_id)

    # Devuelve los usuarios encontrados en la respuesta
    return {
        'statusCode': 200,
        'users': json.dumps(users, use_decimal=True)
    }
