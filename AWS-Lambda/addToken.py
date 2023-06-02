import json
import boto3

dynamodb = boto3.resource('dynamodb')
table = dynamodb.Table('users')


def lambda_handler(event, context):
    # TODO implement
    if 'token' in event and 'user_id' in event:
        token = event['token']
        user_id = event['user_id']

        response = table.get_item(
            Key={
                'user_id': user_id
            }
        )

        # Verifica si el elemento existe en la tabla
        if 'Item' in response:
            existing_item = response['Item']

            # Actualiza el campo connection_id manteniendo los atributos existentes
            existing_item['token'] = token

            # Actualiza el registro en la tabla de usuarios
            table.put_item(Item=existing_item)
            return {
                'statusCode': 200,
            }
        else:
            print(f"No se encontró el user_id: {user_id}")

    return {
        'statusCode': 501,
        'body': json.dumps('event')
    }
